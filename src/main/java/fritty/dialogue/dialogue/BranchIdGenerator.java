package fritty.dialogue.dialogue;

import java.util.Map;
import java.util.Objects;

public final class BranchIdGenerator {

    private BranchIdGenerator() {
    }

    public static String generate(
            String branchName,
            Map<String, ?> existingBranches
    ) {
        Objects.requireNonNull(
                existingBranches,
                "Existing branches cannot be null"
        );

        if (branchName == null || branchName.isBlank()) {
            throw new IllegalArgumentException(
                    "Назва гілки не може бути порожньою."
            );
        }

        String baseId = DialogueIdGenerator.generate(branchName);
        String candidate = baseId;
        int suffix = 2;

        while (existingBranches.containsKey(candidate)) {
            candidate = baseId + "_" + suffix;
            suffix++;
        }

        return DialogueIdValidator.validate(candidate);
    }
}
