package by.siarhiejbahdaniec.playeradsplugin;

import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class PlayerAdsPlugin extends JavaPlugin implements ConfigHolder {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Objects.requireNonNull(getCommand("ad"))
                .setExecutor(new AdCommandExecutor(this));
    }

    @NotNull
    @Override
    public String getString(String key) {
        return getString(key, "");
    }

    @NotNull
    @Override
    public String getString(String key, String def) {
        return getConfig().getString(key, def);
    }
}
