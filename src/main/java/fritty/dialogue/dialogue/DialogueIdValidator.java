package fritty.dialogue.dialogue;

import java.util.Locale;
import java.util.regex.Pattern;

public final class DialogueIdValidator {

    private static final Pattern VALID_ID =
            Pattern.compile("[a-z0-9]+(?:_[a-z0-9]+)*");

    private static final int MAX_LENGTH = 64;

    private DialogueIdValidator() {
    }

    public static boolean isValid(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }

        if (id.length() > MAX_LENGTH) {
            return false;
        }

        return VALID_ID.matcher(id).matches();
    }

    public static String normalize(String id) {
        if (id == null) {
            return "";
        }

        return id.trim().toLowerCase(Locale.ROOT);
    }

    public static String validate(String id) {
        String normalized = normalize(id);

        if (!isValid(normalized)) {
            throw new IllegalArgumentException(
                    "ID має містити лише малі латинські літери, "
                            + "цифри та одинарні підкреслення між словами. "
                            + "Максимальна довжина — " + MAX_LENGTH + " символів."
            );
        }

        return normalized;
    }
}
