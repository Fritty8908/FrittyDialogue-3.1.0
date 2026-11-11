package fritty.dialogue.dialogue;

import java.util.Map;
import java.util.Objects;

public final class BranchTargetValidator {

    private BranchTargetValidator() {
    }

    public static void validate(
            String targetBranchId,
            Map<String, Branch> branches
    ) {
        Objects.requireNonNull(branches, "Branches cannot be null");

        if (targetBranchId == null || targetBranchId.isBlank()) {
            return;
        }

        if (!branches.containsKey(targetBranchId)) {
            throw new IllegalArgumentException(
                    "Цільову гілку не знайдено: " + targetBranchId
            );
        }
    }

    public static boolean exists(
            String targetBranchId,
            Map<String, Branch> branches
    ) {
        return targetBranchId != null
                && !targetBranchId.isBlank()
                && branches != null
                && branches.containsKey(targetBranchId);
    }
}
