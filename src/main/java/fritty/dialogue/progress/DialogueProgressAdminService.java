package fritty.dialogue.progress;

import java.util.Objects;

public final class DialogueProgressAdminService {

    public void resetPlayer(PlayerProgress progress) {
        requireProgress(progress);
        progress.reset();
    }

    public void completeDialogue(
            PlayerProgress progress,
            String dialogueId
    ) {
        requireProgress(progress);
        progress.completeDialogue(requireId(dialogueId));
    }

    public void completeBranch(
            PlayerProgress progress,
            String dialogueId,
            String branchId
    ) {
        requireProgress(progress);
        progress.completeBranch(
                requireId(dialogueId),
                requireId(branchId)
        );
    }

    public void deleteDialogue(
            PlayerProgress progress,
            String dialogueId
    ) {
        requireProgress(progress);
        progress.removeDialogue(requireId(dialogueId));
    }

    public void deleteBranch(
            PlayerProgress progress,
            String dialogueId,
            String branchId
    ) {
        requireProgress(progress);
        progress.removeBranch(
                requireId(dialogueId),
                requireId(branchId)
        );
    }

    private void requireProgress(PlayerProgress progress) {
        Objects.requireNonNull(
                progress,
                "Прогрес гравця не може бути null."
        );
    }

    private String requireId(String value) {
        Objects.requireNonNull(value, "ID не може бути null.");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "ID не може бути порожнім."
            );
        }

        return value;
    }
}
