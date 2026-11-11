package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueNameValidator {

    private DialogueNameValidator() {
    }

    public static void validate(String name) {
        Objects.requireNonNull(name, "Dialogue name cannot be null");

        String normalized = name.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "Назва діалогу не може бути порожньою."
            );
        }

        if (normalized.length() > 64) {
            throw new IllegalArgumentException(
                    "Назва діалогу не може перевищувати 64 символи."
            );
        }
    }

    public static boolean isValid(String name) {
        if (name == null) {
            return false;
        }

        String normalized = name.trim();

        return !normalized.isEmpty()
                && normalized.length() <= 64;
    }
}
