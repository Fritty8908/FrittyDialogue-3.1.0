package fritty.dialogue.progress;

import java.util.Objects;

public final class DialogueProgressRequirement {

    private final DialogueProgressType type;
    private final String dialogueId;
    private final String branchId;
    private final String answerId;

    public DialogueProgressRequirement(
            DialogueProgressType type,
            String dialogueId,
            String branchId,
            String answerId
    ) {
        this.type = Objects.requireNonNull(type);
        this.dialogueId = requireId(dialogueId);

        if (type != DialogueProgressType.DIALOGUE_COMPLETED
                && branchId == null) {
            throw new IllegalArgumentException(
                    "Для цієї умови потрібен ID гілки."
            );
        }

        if (type == DialogueProgressType.ANSWER_SELECTED
                && answerId == null) {
            throw new IllegalArgumentException(
                    "Для вибраної відповіді потрібен її ID."
            );
        }

        this.branchId = branchId;
        this.answerId = answerId;
    }

    public DialogueProgressType getType() {
        return type;
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

    private static String requireId(String value) {
        Objects.requireNonNull(value, "ID не може бути null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "ID не може бути порожнім."
            );
        }

        return value;
    }
}
