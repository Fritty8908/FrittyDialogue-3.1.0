package fritty.dialogue.dialogue;

import java.util.Map;
import java.util.Objects;

public final class BranchCreationService {

    public Branch create(
            String name,
            String npcText,
            Map<String, Branch> existingBranches
    ) {
        Objects.requireNonNull(
                existingBranches,
                "Existing branches cannot be null"
        );

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Назва гілки не може бути порожньою."
            );
        }

        String branchId = BranchIdGenerator.generate(
                name,
                existingBranches
        );

        BranchIdValidator.validate(branchId);

        return new Branch(
                branchId,
                name,
                npcText == null ? "" : npcText,
                false
        );
    }
}
