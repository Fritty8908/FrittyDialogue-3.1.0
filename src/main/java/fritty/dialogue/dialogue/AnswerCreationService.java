package fritty.dialogue.dialogue;

import java.util.Objects;

public final class AnswerCreationService {

    public Answer create(
            String text,
            String targetBranchId,
            Iterable<Answer> existingAnswers
    ) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Текст відповіді не може бути порожнім."
            );
        }

        if (targetBranchId != null && !targetBranchId.isBlank()) {
            DialogueIdValidator.validate(targetBranchId);
        }

        Objects.requireNonNull(
                existingAnswers,
                "Existing answers cannot be null"
        );

        String answerId = AnswerIdGenerator.generate(
                text,
                existingAnswers
        );

        AnswerIdValidator.validate(answerId);

        return new Answer(
                answerId,
                text,
                targetBranchId
        );
    }
}
