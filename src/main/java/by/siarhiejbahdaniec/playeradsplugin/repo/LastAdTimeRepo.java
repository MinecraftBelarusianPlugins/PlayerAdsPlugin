package by.siarhiejbahdaniec.playeradsplugin.repo;

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

    private final boolean saveImmediately;

    public LastAdTimeRepo(@NotNull File dir,
                          @NotNull Logger logger,
                          boolean saveImmediately) {
        file = new File(dir, filename);
        configuration = YamlConfiguration.loadConfiguration(file);

        this.logger = logger;
        this.saveImmediately = saveImmediately;
    }

    public void setLastAdTime(@NotNull String username,
                              @NotNull Long time) {
        configuration.set(username, time);

        if (saveImmediately) {
            saveData();
        }
    }

    @NotNull
    public Long getLastAdTime(@NotNull String username) {
        return configuration.getLong(username, 0L);
    }

    public void saveData() {
        try {
            configuration.save(file);
        } catch (IOException t) {
            logger.log(Level.WARNING, "Failed to save players timestamps for the PlayerAdsPlugin to a file");
        }
    }
}
