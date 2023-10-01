package by.siarhiejbahdaniec.playeradsplugin.repo;

import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigKeys;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LastAdTimeRepo {

    private static final String filename = "timestamps.yml";

    @NotNull
    private final File file;

    @NotNull
    private final YamlConfiguration configuration;

    @NotNull
    private final Logger logger;

    @NotNull
    private final ConfigHolder configHolder;

    public LastAdTimeRepo(@NotNull File dir,
                          @NotNull Logger logger,
                          @NotNull ConfigHolder configHolder) {
        file = new File(dir, filename);
        configuration = YamlConfiguration.loadConfiguration(file);

        this.logger = logger;
        this.configHolder = configHolder;
    }

    public void setLastAdTime(@NotNull String playerName,
                              @NotNull Long time) {
        configuration.set(playerName, time);

        if (configHolder.getBoolean(ConfigKeys.adSaveFileImmediately)) {
            saveData();
        }
    }

    @NotNull
    public Long getLastAdTime(@NotNull String playerName) {
        return configuration.getLong(playerName, 0L);
    }

    public void saveData() {
        try {
            configuration.save(file);
        } catch (IOException t) {
            logger.log(Level.WARNING, "Failed to save players timestamps for the PlayerAdsPlugin to a file");
        }
    }
}
