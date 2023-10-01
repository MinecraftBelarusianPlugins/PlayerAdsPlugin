package by.siarhiejbahdaniec.playeradsplugin.utils;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class StringUtils {

    @NotNull
    public static String getOrEmpty(@Nullable String string) {
        return string == null ? "" : string;
    }
}
