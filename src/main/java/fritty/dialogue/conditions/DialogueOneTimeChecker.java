package fritty.dialogue.conditions;

import fritty.dialogue.progress.PlayerProgress;

import java.util.Objects;

public final class DialogueOneTimeChecker {

    public boolean canStartDialogue(
            PlayerProgress progress,
            String dialogueId,
            boolean oneTime
    ) {
        if (!oneTime) {
            return true;
        }

        return progress == null
                || !progress.hasCompletedDialogue(
                        requireId(dialogueId)
                );
    }

    public boolean canStartBranch(
            PlayerProgress progress,
            String dialogueId,
            String branchId,
            boolean oneTime
    ) {
        if (!oneTime) {
            return true;
        }

        return progress == null
                || !progress.hasCompletedBranch(
                        requireId(dialogueId),
                        requireId(branchId)
                );
    }

    public boolean canSelectAnswer(
            PlayerProgress progress,
            String dialogueId,
            String branchId,
            String answerId,
            boolean oneTime
    ) {
        if (!oneTime) {
            return true;
        }

        return progress == null
                || !progress.hasSelectedAnswer(
                        requireId(dialogueId),
                        requireId(branchId),
                        requireId(answerId)
                );
    }

    private String requireId(String value) {
        Objects.requireNonNull(value, "ID не може бути null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "ID не може бути порожнім."
            );
        }

        return value;
    }
}
