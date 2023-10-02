package by.siarhiejbahdaniec.playeradsplugin.config;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ConfigHolder {

    @NotNull
    String getString(String key);

    @NotNull
    String getString(String key, String def);

    @NotNull
    List<String> getStringList(String key);

    int getInt(String key);

    boolean getBoolean(String key);

    void reloadConfigFromDisk();
}
