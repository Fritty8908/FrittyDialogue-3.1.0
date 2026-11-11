package fritty.dialogue.progress;

import java.util.Objects;

public final class DialogueProgressChecker {

    public boolean isSatisfied(
            PlayerProgress progress,
            DialogueProgressRequirement requirement
    ) {
        Objects.requireNonNull(requirement);

        if (progress == null) {
            return false;
        }

        switch (requirement.getType()) {
            case DIALOGUE_COMPLETED:
                return progress.hasCompletedDialogue(
                        requirement.getDialogueId()
                );

            case BRANCH_COMPLETED:
                return progress.hasCompletedBranch(
                        requirement.getDialogueId(),
                        requirement.getBranchId()
                );

            case ANSWER_SELECTED:
                return progress.hasSelectedAnswer(
                        requirement.getDialogueId(),
                        requirement.getBranchId(),
                        requirement.getAnswerId()
                );

            default:
                return false;
        }
    }
}
