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
            Dialogue dialogue,
            Branch branch,
            String text,
            String targetBranchId
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (!dialogue.getBranches().containsKey(branch.getId())) {
            throw new IllegalArgumentException(
                    "Гілка не належить цьому діалогу."
            );
        }

        if (targetBranchId != null
                && !targetBranchId.isBlank()
                && !dialogue.getBranches().containsKey(targetBranchId)) {
            throw new IllegalArgumentException(
                    "Цільова гілка не існує в цьому діалозі."
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

    public void validate(Dialogue dialogue) {
        DialogueStructureValidator.validate(dialogue);
    }
}
