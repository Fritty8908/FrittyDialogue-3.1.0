package fritty.dialogue.session;

public enum DialogueSessionCommandTiming {
    NONE,
    ON_START,
    AFTER_TEXT;

    public static DialogueSessionCommandTiming fromString(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }

        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return NONE;
        }
    }

    public boolean shouldRunOnStart() {
        return this == ON_START;
    }

    public boolean shouldRunAfterText() {
        return this == AFTER_TEXT;
    }

    public boolean isEnabled() {
        return this != NONE;
    }
}
