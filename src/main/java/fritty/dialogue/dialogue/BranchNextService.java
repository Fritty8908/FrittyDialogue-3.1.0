package fritty.dialogue.dialogue;

import java.util.Map;
import java.util.Objects;

public final class BranchNextService {

    public void setNext(
            Dialogue dialogue,
            Branch branch,
            String targetBranchId
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(branch, "Branch cannot be null");

        Map<String, Branch> branches = dialogue.getBranches();

        if (branches.get(branch.getId()) != branch) {
            throw new IllegalArgumentException(
                    "Гілка не належить цьому діалогу."
            );
        }

        if (targetBranchId == null || targetBranchId.isBlank()) {
            branch.setNextBranchId(null);
            return;
        }

        if (!branches.containsKey(targetBranchId)) {
            throw new IllegalArgumentException(
                    "Цільову гілку не знайдено: " + targetBranchId
            );
        }

        branch.setNextBranchId(targetBranchId);
    }

    public void clearNext(Branch branch) {
        Objects.requireNonNull(branch, "Branch cannot be null");
        branch.setNextBranchId(null);
    }
}
