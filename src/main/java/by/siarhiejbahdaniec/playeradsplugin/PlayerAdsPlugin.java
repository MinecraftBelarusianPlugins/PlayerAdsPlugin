package by.siarhiejbahdaniec.playeradsplugin;

import by.siarhiejbahdaniec.playeradsplugin.command.AdCommandExecutor;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigKeys;
import by.siarhiejbahdaniec.playeradsplugin.repo.LastAdTimeRepo;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class PlayerAdsPlugin extends JavaPlugin implements ConfigHolder {

    @Nullable
    private LastAdTimeRepo lastAdTimeRepo;

    @Override
    public void onEnable() {
        setupConfig();

        var saveImmediately = getConfig().getBoolean(ConfigKeys.adSaveFileImmediately, false);
        lastAdTimeRepo = new LastAdTimeRepo(getDataFolder(), getLogger(), saveImmediately);

        var executor = new AdCommandExecutor(this, lastAdTimeRepo);
        Objects.requireNonNull(getCommand("ad")).setExecutor(executor);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (lastAdTimeRepo != null) {
            lastAdTimeRepo.saveData();
        }
    }

    private void setupConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
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
