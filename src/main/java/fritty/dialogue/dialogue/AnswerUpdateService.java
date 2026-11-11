package fritty.dialogue.dialogue;

import java.util.Objects;

public final class AnswerUpdateService {

    public void updateText(Answer answer, String newText) {
        Objects.requireNonNull(answer, "Answer cannot be null");

        if (newText == null || newText.isBlank()) {
            throw new IllegalArgumentException(
                    "Текст відповіді не може бути порожнім."
            );
        }

        answer.setText(newText.trim());
    }

    public void updateTarget(
            Dialogue dialogue,
            Answer answer,
            String targetBranchId
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(answer, "Answer cannot be null");

        if (targetBranchId != null && !targetBranchId.isBlank()
                && !dialogue.getBranches().containsKey(targetBranchId)) {
            throw new IllegalArgumentException(
                    "Цільову гілку не знайдено: " + targetBranchId
            );
        }

        answer.setTargetBranchId(
                targetBranchId == null || targetBranchId.isBlank()
                        ? null
                        : targetBranchId
        );
    }

    public void updateCommand(Answer answer, String command) {
        Objects.requireNonNull(answer, "Answer cannot be null");

        answer.setCommand(
                command == null || command.isBlank()
                        ? null
                        : command.trim()
        );
    }

    public void updateOneTime(Answer answer, boolean oneTime) {
        Objects.requireNonNull(answer, "Answer cannot be null");
        answer.setOneTime(oneTime);
    }

    public void updateAvailability(
            Answer answer,
            Answer.AvailabilityMode mode
    ) {
        Objects.requireNonNull(answer, "Answer cannot be null");
        answer.setAvailabilityMode(
                Objects.requireNonNull(mode, "Availability mode cannot be null")
        );
    }
}
