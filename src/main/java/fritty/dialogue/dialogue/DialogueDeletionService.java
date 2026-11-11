package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueDeletionService {

    public void deleteBranch(Dialogue dialogue, String branchId) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        if (branchId == null || branchId.isBlank()) {
            throw new IllegalArgumentException(
                    "ID гілки не може бути порожнім."
            );
        }

        if (!dialogue.getBranches().containsKey(branchId)) {
            throw new IllegalArgumentException(
                    "Гілку не знайдено: " + branchId
            );
        }

        if (dialogue.getBranches().size() == 1) {
            throw new IllegalStateException(
                    "Не можна видалити останню гілку діалогу."
            );
        }

        dialogue.getBranches().remove(branchId);

        dialogue.getBranches().values().forEach(branch -> {
            if (branchId.equals(branch.getNextBranchId())) {
                branch.setNextBranchId(null);
            }

            branch.getAnswers().removeIf(answer ->
                    branchId.equals(answer.getTargetBranchId())
            );
        });
    }

    public void deleteAnswer(Branch branch, String answerId) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (answerId == null || answerId.isBlank()) {
            throw new IllegalArgumentException(
                    "ID відповіді не може бути порожнім."
            );
        }

        boolean removed = branch.getAnswers().removeIf(answer ->
                answerId.equals(answer.getId())
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Відповідь не знайдено: " + answerId
            );
        }
    }
}
