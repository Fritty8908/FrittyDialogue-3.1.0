package fritty.dialogue.dialogue;

import java.util.Objects;

public final class BranchRenameService {

    public void rename(Branch branch, String newName) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException(
                    "Нова назва гілки не може бути порожньою."
            );
        }

        branch.setName(newName.trim());
    }
}
