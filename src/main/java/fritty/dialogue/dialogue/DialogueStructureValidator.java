package fritty.dialogue.dialogue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DialogueStructureValidator {

    private DialogueStructureValidator() {
    }

    public static void validate(Dialogue dialogue) {
        if (dialogue == null) {
            throw new IllegalArgumentException(
                    "Діалог не може бути порожнім."
            );
        }

        Map<String, Branch> branches = dialogue.getBranches();

        if (branches == null) {
            throw new IllegalStateException(
                    "Список гілок діалогу не ініціалізований."
            );
        }

        Set<String> branchIds = new HashSet<>();

        for (Map.Entry<String, Branch> entry : branches.entrySet()) {
            Branch branch = entry.getValue();

            if (branch == null) {
                throw new IllegalStateException(
                        "Діалог містить порожню гілку."
                );
            }

            String branchId = BranchIdValidator.validate(branch.getId());

            if (!entry.getKey().equals(branchId)) {
                throw new IllegalStateException(
                        "ID гілки не збігається з ключем у списку гілок: "
                                + branchId
                );
            }

            if (!branchIds.add(branchId)) {
                throw new IllegalStateException(
                        "Знайдено повторний ID гілки: " + branchId
                );
            }

            validateBranch(branch, branches);
        }
    }

    private static void validateBranch(
            Branch branch,
            Map<String, Branch> branches
    ) {
        String nextBranchId = branch.getNextBranchId();

        if (nextBranchId != null && !nextBranchId.isBlank()
                && !branches.containsKey(nextBranchId)) {
            throw new IllegalStateException(
                    "Гілка " + branch.getId()
                            + " посилається на відсутню гілку "
                            + nextBranchId
            );
        }

        if (branch.getAnswers() == null) {
            throw new IllegalStateException(
                    "Список відповідей гілки "
                            + branch.getId()
                            + " не ініціалізований."
            );
        }

        Set<String> answerIds = new HashSet<>();

        for (Answer answer : branch.getAnswers()) {
            if (answer == null) {
                throw new IllegalStateException(
                        "Гілка " + branch.getId()
                                + " містить порожню відповідь."
                );
            }

            String answerId = AnswerIdValidator.validate(answer.getId());

            if (!answerIds.add(answerId)) {
                throw new IllegalStateException(
                        "Повторний ID відповіді " + answerId
                                + " у гілці " + branch.getId()
                );
            }

            String targetBranchId = answer.getTargetBranchId();

            if (targetBranchId != null
                    && !targetBranchId.isBlank()
                    && !branches.containsKey(targetBranchId)) {
                throw new IllegalStateException(
                        "Відповідь " + answerId
                                + " посилається на відсутню гілку "
                                + targetBranchId
                );
            }
        }
    }
}
