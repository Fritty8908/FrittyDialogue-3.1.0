package fritty.dialogue.dialogue;

public enum SettingKey {

    HIDE_PLAYERS("hide-players", Type.BOOLEAN, true),
    ONE_TIME("one-time", Type.BOOLEAN, false),
    SEE_THROUGH_BLOCKS("see-through-blocks", Type.BOOLEAN, false),

    LOCATION("location", Type.STRING, ""),
    CAMERA_LOCATION("camera-location", Type.STRING, ""),

    SMOOTH_TELEPORT("smooth-teleport", Type.BOOLEAN, true),

    CHAR_DELAY("char-delay", Type.INTEGER, 2),
    CHAR_SOUND("char-sound", Type.STRING, ""),
    CHAR_PITCH("char-pitch", Type.DOUBLE, 1.0),
    SOUND_VOLUME("sound-volume", Type.DOUBLE, 1.0),

    PER_CHAR("per-char", Type.BOOLEAN, true),

    BACKGROUND_COLOR("background-color", Type.STRING, "#000000"),
    BACKGROUND_ALPHA("background-alpha", Type.INTEGER, 160),

    LINE_WIDTH("line-width", Type.INTEGER, 300),
    TEXT_SCALE("text-scale", Type.DOUBLE, 1.0),

    USE_SLOWNESS("use-slowness", Type.BOOLEAN, false),
    MAKE_INVISIBLE("make-invisible", Type.BOOLEAN, false),

    SAVE_START_LOCATION("save-start-location", Type.BOOLEAN, true),
    ACTIONBAR_HINTS("actionbar-hints", Type.BOOLEAN, true),

    FONT("font", Type.STRING, "");

    private final String key;
    private final Type type;
    private final Object defaultValue;

    SettingKey(String key, Type type, Object defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public enum Type {
        STRING,
        BOOLEAN,
        INTEGER,
        DOUBLE
    }

    public static SettingKey fromKey(String key) {
        if (key == null) {
            return null;
        }

        for (SettingKey setting : values()) {
            if (setting.key.equalsIgnoreCase(key)) {
                return setting;
            }
        }

        return null;
    }
}
