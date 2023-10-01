package by.siarhiejbahdaniec.playeradsplugin;

import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdCommandExecutor implements CommandExecutor {

    @NotNull
    private final ConfigHolder configHolder;

    public AdCommandExecutor(@NotNull ConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (sender instanceof Player) {
            var playerName = sender.getName();
            var message = String.join(" ", args);
            Bukkit.getServer().broadcastMessage(String.format("AD from %s: %s", playerName, message));
        } else {
            sender.sendMessage(configHolder.getString(ConfigKeys.Resources.commandOnlyForPlayers));
        }
        return true;
    }

}
