package by.siarhiejbahdaniec.playeradsplugin.command;

import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigKeys;
import by.siarhiejbahdaniec.playeradsplugin.repo.LastAdTimeRepo;
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
            var playerName = sender.getName();

            var time = System.currentTimeMillis();
            var lastTimestamp = lastAdTimeRepo.getLastAdTime(playerName);
            var timeDifference = time - lastTimestamp;
            long threshold = TimeUnit.SECONDS.toMillis(configHolder.getLong(ConfigKeys.adThresholdPerPlayer));

            if (timeDifference > threshold) {
                var message = String.join(" ", args);
                postUserAd(message, playerName);
                lastAdTimeRepo.setLastAdTime(playerName, time);
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

    private void postUserAd(String message, String playerName) {
        var prefix = configHolder.getString(ConfigKeys.adPrefix);
        var postfix = configHolder.getString(ConfigKeys.adPostfix);
        var signature = configHolder.getString(ConfigKeys.adPlayerSignatureFormat).formatted(playerName);
        var formattedMessage = prefix + message + signature + postfix;
        Bukkit.getServer().broadcastMessage(formattedMessage);
    }
}
