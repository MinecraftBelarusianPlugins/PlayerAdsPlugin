package by.siarhiejbahdaniec.playeradsplugin.config;

public class ConfigKeys {

    public static final String adPrefix = "ad-prefix";
    public static final String adPostfix = "ad-postfix";
    public static final String adPlayerSignatureFormat = "ad-player-signature-format";
    public static final String adUseMultiLine = "ad-use-multi-line";

    public static final String adThresholdPerPlayer = "ad-threshold-per-player";
    public static final String adSaveFileImmediately = "ad-threshold-save-file-immediately";
    public static final String adMaxMessageLength = "ad-max-message-length";
    public static final String adMinMessageLength = "ad-min-message-length";
    public static final String adUseLuckPermsPlaceholders = "ad-use-luck-perms-placeholders";

    public static final String logging = "logging";

    public static class Resources {
        private static final String _prefix = "resources.";

        public static final String hoursPlurals = _prefix + "hours-plurals";
        public static final String minutesPlurals = _prefix + "minutes-plurals";
        public static final String secondsPlurals = _prefix + "seconds-plurals";

        public static final String commandOnlyForPlayers = _prefix + "command-only-for-players";
        public static final String waitToUseCommand = _prefix + "wait-to-use-command";
        public static final String messageTooLarge = _prefix + "message-too-large";
        public static final String messageTooSmall = _prefix + "message-too-small";

        public static final String configReloaded = _prefix + "config-reloaded";
        public static final String playerNotFound = _prefix + "player-not-found";
        public static final String playerTimerReset = _prefix + "player-timer-reset";

        public static final String invalidCommand = _prefix + "invalid-command";
        public static final String invalidCommandReset = _prefix + "invalid-command-reset";
        public static final String invalidCommandAd = _prefix + "invalid-command-ad";
    }
}
