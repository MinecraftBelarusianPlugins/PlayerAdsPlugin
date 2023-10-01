package by.siarhiejbahdaniec.playeradsplugin.command;

import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigKeys;
import by.siarhiejbahdaniec.playeradsplugin.repo.LastAdTimeRepo;
import by.siarhiejbahdaniec.playeradsplugin.utils.StringUtils;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class AdCommandExecutor implements CommandExecutor {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    @NotNull
    private final ConfigHolder configHolder;

    @NotNull
    private final LastAdTimeRepo lastAdTimeRepo;

    public AdCommandExecutor(@NotNull ConfigHolder configHolder,
                             @NotNull LastAdTimeRepo lastAdTimeRepo) {
        this.configHolder = configHolder;
        this.lastAdTimeRepo = lastAdTimeRepo;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (sender instanceof Player) {
            var playerName = obtainPlayerName((Player) sender);

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
                    sender.sendMessage(error);
                } else if (minLength >= 0 && messageLength < minLength) {
                    var error = configHolder.getString(ConfigKeys.Resources.messageTooSmall)
                            .formatted(minLength);
                    sender.sendMessage(error);
                } else {
                    postUserAd(message, playerName);
                    lastAdTimeRepo.setLastAdTime(playerName, time);
                }
            } else {
                var timeLabel = simpleDateFormat.format(threshold - timeDifference);
                var message = configHolder.getString(ConfigKeys.Resources.waitToUseCommand).formatted(timeLabel);
                sender.sendMessage(message);
            }
        } else {
            sender.sendMessage(configHolder.getString(ConfigKeys.Resources.commandOnlyForPlayers));
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
        var formattedMessage = prefix + message + signature + postfix;
        Bukkit.getServer().broadcastMessage(formattedMessage);
    }
}
