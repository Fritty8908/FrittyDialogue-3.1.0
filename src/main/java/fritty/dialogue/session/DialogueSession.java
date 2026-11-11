package fritty.dialogue.session;

import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

public final class DialogueSession {

    private final UUID playerId;
    private final Dialogue dialogue;
    private final Location originalLocation;

    private String currentBranchId;
    private int selectedAnswerIndex;

    private boolean textFinished;
    private boolean active;

    public DialogueSession(
            UUID playerId,
            Dialogue dialogue,
            String startingBranchId,
            Location originalLocation
    ) {
        this.playerId = Objects.requireNonNull(
                playerId,
                "Player UUID cannot be null"
        );

        this.dialogue = Objects.requireNonNull(
                dialogue,
                "Dialogue cannot be null"
        );

        this.originalLocation = originalLocation == null
                ? null
                : originalLocation.clone();

        setCurrentBranchId(startingBranchId);

        this.selectedAnswerIndex = 0;
        this.textFinished = false;
        this.active = true;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public String getDialogueId() {
        return dialogue.getId();
    }

    public String getCurrentBranchId() {
        return currentBranchId;
    }

    public Branch getCurrentBranch() {
        return dialogue.getBranch(currentBranchId);
    }

    public void setCurrentBranchId(String branchId) {
        if (branchId == null || branchId.isBlank()) {
            throw new IllegalArgumentException(
                    "Branch ID cannot be empty"
            );
        }

        if (!dialogue.hasBranch(branchId)) {
            throw new IllegalArgumentException(
                    "Branch does not exist: " + branchId
            );
        }

        this.currentBranchId = branchId;
        this.selectedAnswerIndex = 0;
        this.textFinished = false;
    }

    public Location getOriginalLocation() {
        return originalLocation == null
                ? null
                : originalLocation.clone();
    }

    public int getSelectedAnswerIndex() {
        return selectedAnswerIndex;
    }

    public void setSelectedAnswerIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException(
                    "Answer index cannot be negative"
            );
        }

        this.selectedAnswerIndex = index;
    }

    public void selectPreviousAnswer(int answerCount) {
        if (answerCount <= 0) {
            selectedAnswerIndex = 0;
            return;
        }

        selectedAnswerIndex--;

        if (selectedAnswerIndex < 0) {
            selectedAnswerIndex = answerCount - 1;
        }
    }

    public void selectNextAnswer(int answerCount) {
        if (answerCount <= 0) {
            selectedAnswerIndex = 0;
            return;
        }

        selectedAnswerIndex++;

        if (selectedAnswerIndex >= answerCount) {
            selectedAnswerIndex = 0;
        }
    }

    public boolean isTextFinished() {
        return textFinished;
    }

    public void setTextFinished(boolean finished) {
        this.textFinished = finished;
    }

    public boolean isActive() {
        return active;
    }

    public void end() {
        this.active = false;
    }
}
