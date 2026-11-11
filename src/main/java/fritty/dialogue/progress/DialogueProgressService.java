package fritty.dialogue.progress;

import java.util.UUID;

public final class DialogueProgressService {

    public boolean hasCompletedDialogue(
            PlayerProgress progress,
            String dialogueId
    ) {
        return progress != null
                && progress.hasCompletedDialogue(dialogueId);
    }

    public boolean hasCompletedBranch(
            PlayerProgress progress,
            String dialogueId,
            String branchId
    ) {
        return progress != null
                && progress.hasCompletedBranch(dialogueId, branchId);
    }

    public boolean hasSelectedAnswer(
            PlayerProgress progress,
            String dialogueId,
            String branchId,
            String answerId
    ) {
        return progress != null
                && progress.hasSelectedAnswer(
                        dialogueId,
                        branchId,
                        answerId
                );
    }

    public void completeDialogue(
            PlayerProgress progress,
            String dialogueId
    ) {
        requireProgress(progress);
        progress.completeDialogue(dialogueId);
    }

    public void completeBranch(
            PlayerProgress progress,
            String dialogueId,
            String branchId
    ) {
        requireProgress(progress);
        progress.completeBranch(dialogueId, branchId);
    }

    public void selectAnswer(
            PlayerProgress progress,
            String dialogueId,
            String branchId,
            String answerId
    ) {
        requireProgress(progress);
        progress.selectAnswer(dialogueId, branchId, answerId);
    }

    public void reset(PlayerProgress progress) {
        requireProgress(progress);
        progress.reset();
    }

    private void requireProgress(PlayerProgress progress) {
        if (progress == null) {
            throw new IllegalArgumentException(
                    "Прогрес гравця не може бути null."
            );
        }
    }
}
