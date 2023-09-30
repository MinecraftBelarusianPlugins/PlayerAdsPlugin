package by.siarhiejbahdaniec.playeradsplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {
        if (sender instanceof Player) {
            var playerName = sender.getName();
            var message = args[0];

            Bukkit.getServer().broadcastMessage(String.format("AD from %s: %s", playerName, message));
        } else {
            sender.sendMessage("Гэтая каманда толькі для гульцоў.");
        }
        return true;
    }

}
