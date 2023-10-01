package by.siarhiejbahdaniec.playeradsplugin.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigHolder {

    @NotNull
    public String getString(String key);

    @NotNull
    public String getString(String key, String def);

    public long getLong(String key);

    public boolean getBoolean(String key);
}
