package by.siarhiejbahdaniec.playeradsplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PlayerAdsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("ad")).setExecutor(new AdCommandExecutor());
    }
}
