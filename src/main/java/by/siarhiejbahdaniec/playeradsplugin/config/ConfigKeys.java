package by.siarhiejbahdaniec.playeradsplugin.config;

public class ConfigKeys {

    public static final String adPrefix = "ad-prefix";
    public static final String adPostfix = "ad-postfix";
    public static final String adPlayerSignatureFormat = "ad-player-signature-format";

    public static final String adThresholdPerPlayer = "ad-threshold-per-player";
    public static final String adSaveFileImmediately = "ad-threshold-save-file-immediately";
    public static final String adMaxMessageLength = "ad-max-message-length";

    public static class Resources {
        private static final String _prefix = "resources.";
        public static final String commandOnlyForPlayers = _prefix + "command-only-for-players";
        public static final String waitToUseCommand = _prefix + "wait-to-use-command";
        public static final String messageTooLarge = _prefix + "message-too-large";
    }
}
