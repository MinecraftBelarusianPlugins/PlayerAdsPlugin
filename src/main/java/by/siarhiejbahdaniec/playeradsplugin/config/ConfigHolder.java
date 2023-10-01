package by.siarhiejbahdaniec.playeradsplugin.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigHolder {

    @NotNull
    public String getString(String key);

    @NotNull
    public String getString(String key, String def);
}
