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
            var message = String.join(" ", args);
            var prefix = configHolder.getString(ConfigKeys.adPrefix);
            var postfix = configHolder.getString(ConfigKeys.adPostfix);
            var formattedMessage = String.format("%s%s%s", prefix, message, postfix);
            Bukkit.getServer().broadcastMessage(formattedMessage);
        } else {
            sender.sendMessage(configHolder.getString(ConfigKeys.Resources.commandOnlyForPlayers));
        }
        return true;
    }
}
