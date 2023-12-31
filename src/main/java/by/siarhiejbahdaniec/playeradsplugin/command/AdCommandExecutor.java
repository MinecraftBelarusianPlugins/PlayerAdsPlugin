package by.siarhiejbahdaniec.playeradsplugin.command;

import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigKeys;
import by.siarhiejbahdaniec.playeradsplugin.format.TimeFormatter;
import by.siarhiejbahdaniec.playeradsplugin.repo.LastAdTimeRepo;
import by.siarhiejbahdaniec.playeradsplugin.utils.ColorUtils;
import by.siarhiejbahdaniec.playeradsplugin.utils.StringUtils;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdCommandExecutor implements CommandExecutor {

    @NotNull
    private final ConfigHolder configHolder;

    @NotNull
    private final LastAdTimeRepo lastAdTimeRepo;

    @NotNull
    private final TimeFormatter timeFormatter;

    private static final Pattern permissionPattern = Pattern.compile("player\\.ads\\.([0-9]*)");

    private final static String OPERATOR_MESSAGE_PREFIX = ChatColor.GOLD +
            ChatColor.ITALIC.toString() +
            "[PlayerAdsPlugin] ";

    public AdCommandExecutor(@NotNull ConfigHolder configHolder,
                             @NotNull LastAdTimeRepo lastAdTimeRepo,
                             @NotNull TimeFormatter timeFormatter) {
        this.configHolder = configHolder;
        this.lastAdTimeRepo = lastAdTimeRepo;
        this.timeFormatter = timeFormatter;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(
                    ColorUtils.format(
                            configHolder.getString(ConfigKeys.Resources.invalidCommand)));
            sender.sendMessage(
                    ColorUtils.format(
                            configHolder.getString(ConfigKeys.Resources.invalidCommandAd)
                                    .formatted(label)));
            return true;
        }
        if (handleReload(sender, args)) {
            return true;
        }
        if (handleReset(sender, args)) {
            return true;
        }
        if (sender instanceof Player player) {
            var playerName = sender.getName();
            var decoratedPlayerName = obtainPlayerName(player);

            var time = System.currentTimeMillis();
            var lastTimestamp = lastAdTimeRepo.getLastAdTime(playerName);
            var timeDifference = time - lastTimestamp;
            long threshold = obtainPlayerThreshold(player);

            checkAndLog(
                    Level.INFO,
                    "%s's threshold in millis= %d".formatted(playerName, threshold)
            );

            if (timeDifference >= threshold) {
                var message = String.join(" ", args);

                var maxLength = configHolder.getInt(ConfigKeys.adMaxMessageLength);
                var minLength = configHolder.getInt(ConfigKeys.adMinMessageLength);
                var messageLength = message.length();

                if (messageLength > maxLength && maxLength >= 0) {
                    var error = configHolder.getString(ConfigKeys.Resources.messageTooLarge)
                            .formatted(maxLength);
                    sender.sendMessage(ColorUtils.format(error));
                } else if (minLength >= 0 && messageLength < minLength) {
                    var error = configHolder.getString(ConfigKeys.Resources.messageTooSmall)
                            .formatted(minLength);
                    sender.sendMessage(ColorUtils.format(error));
                } else {
                    postUserAd(message, decoratedPlayerName);
                    lastAdTimeRepo.setLastAdTime(playerName, time);
                }
            } else {
                var timeLabel = timeFormatter.format(threshold - timeDifference);
                var message = configHolder.getString(ConfigKeys.Resources.waitToUseCommand).formatted(timeLabel);
                sender.sendMessage(ColorUtils.format(message));
            }
        } else {
            sender.sendMessage(ColorUtils.format(configHolder.getString(ConfigKeys.Resources.commandOnlyForPlayers)));
        }
        return true;
    }

    private String obtainPlayerName(Player player) {
        var playerName = player.getName();
        if (configHolder.getBoolean(ConfigKeys.adUseLuckPermsPlaceholders)) {
            var pluginManager = Bukkit.getServer().getPluginManager();
            if (pluginManager.isPluginEnabled("LuckPerms")) {
                var provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) {
                    var api = provider.getProvider();
                    var metaData = api.getPlayerAdapter(Player.class).getMetaData(player);
                    return StringUtils.getOrEmpty(metaData.getPrefix()) +
                            playerName +
                            StringUtils.getOrEmpty(metaData.getSuffix());
                }
            }
        }
        return playerName;
    }

    private long obtainPlayerThreshold(Player player) {
        var threshold = player.getEffectivePermissions()
                .stream()
                .map(info -> permissionPattern.matcher(info.getPermission()))
                .filter(Matcher::matches)
                .map(matcher -> Integer.parseInt(matcher.group(1)))
                .min(Comparator.naturalOrder())
                .orElseGet(() -> configHolder.getInt(ConfigKeys.adThresholdPerPlayer));
        return TimeUnit.SECONDS.toMillis(threshold);
    }

    private void postUserAd(String message, String playerName) {
        var prefix = configHolder.getString(ConfigKeys.adPrefix);
        var postfix = configHolder.getString(ConfigKeys.adPostfix);
        var signature = configHolder.getString(ConfigKeys.adPlayerSignatureFormat).formatted(playerName);
        var formattedMessage = String.join(ChatColor.RESET.toString(), prefix, message, signature, postfix);
        var coloredMessage = (ColorUtils.format(formattedMessage));
        if (configHolder.getBoolean(ConfigKeys.adUseMultiLine)) {
            for (var line : coloredMessage.split("\\\\n")) {
                Bukkit.getServer().broadcastMessage(line);
            }
        } else {
            Bukkit.getServer().broadcastMessage(coloredMessage);
        }
    }

    private boolean handleReload(@NotNull CommandSender sender,
                                 @NotNull String[] args) {

        if (sender.isOp() && args[0].equals("reload")) {
            configHolder.reloadConfigFromDisk();
            sender.sendMessage(OPERATOR_MESSAGE_PREFIX +
                    ColorUtils.format(configHolder.getString(ConfigKeys.Resources.configReloaded)));
            return true;
        }

        return false;
    }

    private boolean handleReset(@NotNull CommandSender sender,
                                 @NotNull String[] args) {

        if (sender.isOp() && args[0].equals("reset")) {
            if (args.length < 2) {
                sender.sendMessage(OPERATOR_MESSAGE_PREFIX +
                        ColorUtils.format(configHolder.getString(ConfigKeys.Resources.invalidCommand)));
                sender.sendMessage(OPERATOR_MESSAGE_PREFIX +
                        ColorUtils.format(configHolder.getString(ConfigKeys.Resources.invalidCommandReset)));
            } else {
                for (int i = 1; i < args.length; i++) {
                    var playerName = args[i];
                    var player = Bukkit.getServer().getPlayer(playerName);
                    if (player != null) {
                        lastAdTimeRepo.setLastAdTime(playerName, 0L);
                        sender.sendMessage(OPERATOR_MESSAGE_PREFIX +
                                ColorUtils.format(
                                        configHolder.getString(ConfigKeys.Resources.playerTimerReset)
                                                .formatted(playerName)));
                    } else {
                        sender.sendMessage(OPERATOR_MESSAGE_PREFIX +
                                ColorUtils.format(
                                        configHolder.getString(ConfigKeys.Resources.playerNotFound)
                                                .formatted(playerName)));
                    }
                }
            }
            return true;
        }

        return false;
    }

    private void checkAndLog(Level level, String message) {
        if (configHolder.getBoolean(ConfigKeys.logging)) {
            Bukkit.getLogger().log(level, message);
        }
    }
}
