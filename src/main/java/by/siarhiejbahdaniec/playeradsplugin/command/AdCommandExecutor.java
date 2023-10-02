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

import java.util.concurrent.TimeUnit;

public class AdCommandExecutor implements CommandExecutor {

    @NotNull
    private final ConfigHolder configHolder;

    @NotNull
    private final LastAdTimeRepo lastAdTimeRepo;

    @NotNull
    private final TimeFormatter timeFormatter;

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
        if (handleReload(sender, args)) {
            return true;
        }
        if (handleReset(sender, args)) {
            return true;
        }
        if (sender instanceof Player) {
            var playerName = sender.getName();
            var decoratedPlayerName = obtainPlayerName((Player) sender);

            var time = System.currentTimeMillis();
            var lastTimestamp = lastAdTimeRepo.getLastAdTime(playerName);
            var timeDifference = time - lastTimestamp;
            long threshold = TimeUnit.SECONDS.toMillis(configHolder.getInt(ConfigKeys.adThresholdPerPlayer));

            if (timeDifference > threshold) {
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
            sender.sendMessage(ChatColor.GOLD +
                    ChatColor.BOLD.toString() +
                    "[PlayerAdsPlugin] " +
                    configHolder.getString(ConfigKeys.Resources.configReloaded));
            return true;
        }

        return false;
    }

    private boolean handleReset(@NotNull CommandSender sender,
                                 @NotNull String[] args) {

        if (sender.isOp() && args[0].equals("reset")) {
            var playerName = args[1];
            lastAdTimeRepo.setLastAdTime(playerName, 0L);
            sender.sendMessage("Player's timer was reset!");
            return true;
        }

        return false;
    }
}
