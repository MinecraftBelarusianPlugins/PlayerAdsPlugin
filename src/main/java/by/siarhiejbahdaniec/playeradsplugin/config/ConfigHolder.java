package by.siarhiejbahdaniec.playeradsplugin.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigHolder {

    @NotNull
    String getString(String key);

    @NotNull
    String getString(String key, String def);

    int getInt(String key);

    boolean getBoolean(String key);
}
