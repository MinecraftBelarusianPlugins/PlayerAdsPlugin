package by.siarhiejbahdaniec.playeradsplugin.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static String format(String msg) {
        for(Matcher match = pattern.matcher(msg); match.find(); match = pattern.matcher(msg)) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, ChatColor.of(color.replace("&#", "#")) + "");
        }

        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
