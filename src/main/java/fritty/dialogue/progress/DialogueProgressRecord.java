package fritty.dialogue.progress;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class DialogueProgressRecord {

    private final UUID playerId;
    private final String dialogueId;
    private final String branchId;
    private final String answerId;
    private final Instant completedAt;

    public DialogueProgressRecord(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId,
            Instant completedAt
    ) {
        this.playerId = Objects.requireNonNull(playerId);
        this.dialogueId = requireText(dialogueId, "dialogueId");
        this.branchId = branchId;
        this.answerId = answerId;
        this.completedAt = Objects.requireNonNull(completedAt);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getDialogueId() {
        return dialogueId;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public boolean isDialogueRecord() {
        return branchId == null && answerId == null;
    }

    public boolean isBranchRecord() {
        return branchId != null && answerId == null;
    }

    public boolean isAnswerRecord() {
        return answerId != null;
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " cannot be blank"
            );
        }

        return value;
    }
}
