package fritty.dialogue.dialogue;

import java.util.Objects;

public final class AnswerTargetValidator {

    private AnswerTargetValidator() {
    }

    public static void validate(
            Answer answer,
            Dialogue dialogue
    ) {
        Objects.requireNonNull(answer, "Answer cannot be null");
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        String targetId = answer.getTargetBranchId();

        if (targetId == null || targetId.isBlank()) {
            return;
        }

        if (!dialogue.getBranches().containsKey(targetId)) {
            throw new IllegalArgumentException(
                    "Відповідь посилається на неіснуючу гілку: "
                            + targetId
            );
        }
    }

    public static boolean isValid(
            Answer answer,
            Dialogue dialogue
    ) {
        if (answer == null || dialogue == null) {
            return false;
        }

        String targetId = answer.getTargetBranchId();

        return targetId == null
                || targetId.isBlank()
                || dialogue.getBranches().containsKey(targetId);
    }
}
