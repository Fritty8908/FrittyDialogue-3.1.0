package fritty.dialogue.dialogue;

import java.util.Objects;

public final class BranchUpdateService {

    public void updateName(Branch branch, String name) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Назва гілки не може бути порожньою."
            );
        }

        branch.setName(name.trim());
    }

    public void updateNpcText(Branch branch, String npcText) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        branch.setNpcText(npcText == null ? "" : npcText);
    }

    public void updateCommand(Branch branch, String command) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        branch.setCommand(
                command == null || command.isBlank()
                        ? null
                        : command.trim()
        );
    }

    public void updateCommandTiming(
            Branch branch,
            Branch.CommandTiming timing
    ) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        branch.setCommandTiming(
                Objects.requireNonNull(timing, "Command timing cannot be null")
        );
    }

    public void updateNextBranch(
            Dialogue dialogue,
            Branch branch,
            String nextBranchId
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (dialogue.getBranches().get(branch.getId()) != branch) {
            throw new IllegalArgumentException(
                    "Гілка не належить цьому діалогу."
            );
        }

        if (nextBranchId != null
                && !nextBranchId.isBlank()
                && !dialogue.getBranches().containsKey(nextBranchId)) {
            throw new IllegalArgumentException(
                    "Цільову гілку не знайдено: " + nextBranchId
            );
        }

        branch.setNextBranchId(
                nextBranchId == null || nextBranchId.isBlank()
                        ? null
                        : nextBranchId
        );
    }
}
