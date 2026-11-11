package fritty.dialogue.dialogue;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class BranchIdValidator {

    private static final Pattern VALID_ID =
            Pattern.compile("[a-zA-Z0-9_-]+");

    private BranchIdValidator() {
    }

    public static void validate(String id) {
        Objects.requireNonNull(id, "Branch ID cannot be null");

        if (id.isBlank()) {
            throw new IllegalArgumentException(
                    "ID гілки не може бути порожнім."
            );
        }

        if (!VALID_ID.matcher(id).matches()) {
            throw new IllegalArgumentException(
                    "ID гілки може містити лише латинські літери, цифри, "
                            + "дефіс і нижнє підкреслення."
            );
        }
    }

    public static void validate(
            String id,
            Map<String, Branch> existingBranches
    ) {
        validate(id);
        Objects.requireNonNull(
                existingBranches,
                "Branches map cannot be null"
        );

        if (existingBranches.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Гілка з таким ID вже існує: " + id
            );
        }
    }

    public static boolean isValid(String id) {
        return id != null
                && !id.isBlank()
                && VALID_ID.matcher(id).matches();
    }
}
