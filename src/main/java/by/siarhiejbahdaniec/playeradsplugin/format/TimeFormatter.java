package by.siarhiejbahdaniec.playeradsplugin.format;

import by.siarhiejbahdaniec.playeradsplugin.config.ConfigHolder;
import by.siarhiejbahdaniec.playeradsplugin.config.ConfigKeys;
import org.jetbrains.annotations.NotNull;

public class TimeFormatter {

    @NotNull
    private final ConfigHolder configHolder;

    public TimeFormatter(@NotNull ConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    @NotNull
    public String format(long millis) {
        var hours = millis / (60 * 60 * 1000);
        var minutes = millis / (60 * 1000) - hours * 60;
        var seconds = millis / 1000 - hours * 60 * 60 - minutes * 60;

        try {
            return String.join(" ", formatTimeUnit(ConfigKeys.Resources.hoursPlurals, hours),
                            formatTimeUnit(ConfigKeys.Resources.minutesPlurals, minutes),
                            formatTimeUnit(ConfigKeys.Resources.secondsPlurals, seconds))
                    .trim();
        } catch (Throwable throwable) {
            var builder = new StringBuilder();
            if (hours > 0) {
                builder.append("%02d:".formatted(hours));
            }
            if (hours > 0 || minutes > 0) {
                builder.append("%02d:".formatted(minutes));
            }
            if (hours > 0 || minutes > 0 || seconds > 0) {
                builder.append("%02d".formatted(seconds));
            }
            return builder.toString();
        }
    }

    private String formatTimeUnit(String pluralKey, long value) {
        if (value == 0)
            return "";

        var pluralIndex = (value % 10 == 1 && value % 100 != 11 ? 0 : value % 10 >= 2 && value % 10 <= 4
                && (value % 100 < 10 || value % 100 >= 20) ? 1 : 2);
        var plural = configHolder.getStringList(pluralKey).get(pluralIndex);

        return "%d %s".formatted(value, plural);
    }
}
