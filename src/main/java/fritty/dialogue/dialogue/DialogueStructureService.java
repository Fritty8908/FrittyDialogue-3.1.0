package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueStructureService {

    private final BranchCreationService branchCreationService;
    private final AnswerCreationService answerCreationService;

    public DialogueStructureService() {
        this.branchCreationService = new BranchCreationService();
        this.answerCreationService = new AnswerCreationService();
    }

    public Branch createBranch(
            Dialogue dialogue,
            String name,
            String npcText
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        Branch branch = branchCreationService.create(
                name,
                npcText,
                dialogue.getBranches()
        );

        dialogue.getBranches().put(branch.getId(), branch);

        return branch;
    }

    public Answer createAnswer(
            Branch branch,
            String text,
            String targetBranchId
    ) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (targetBranchId != null
                && !targetBranchId.isBlank()
                && !branch.getId().equals(targetBranchId)) {
            throw new IllegalArgumentException(
                    "Перехід має вести до іншої гілки."
            );
        }

        Answer answer = answerCreationService.create(
                text,
                targetBranchId,
                branch.getAnswers()
        );

        branch.getAnswers().add(answer);

        return answer;
    }
}
