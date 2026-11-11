package fritty.dialogue.dialogue;

import java.util.regex.Pattern;

public final class AnswerIdValidator {

    private static final Pattern VALID_ID =
            Pattern.compile("^[a-z0-9]+(?:_[a-z0-9]+)*$");

    private AnswerIdValidator() {
    }

    public static String validate(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "ID відповіді не може бути порожнім."
            );
        }

        if (id.length() > 64) {
            throw new IllegalArgumentException(
                    "ID відповіді не може бути довшим за 64 символи."
            );
        }

        if (!VALID_ID.matcher(id).matches()) {
            throw new IllegalArgumentException(
                    "ID відповіді може містити лише малі латинські літери, цифри та підкреслення."
            );
        }

        return id;
    }

    public static boolean isValid(String id) {
        if (id == null || id.isBlank() || id.length() > 64) {
            return false;
        }

        return VALID_ID.matcher(id).matches();
    }
}
