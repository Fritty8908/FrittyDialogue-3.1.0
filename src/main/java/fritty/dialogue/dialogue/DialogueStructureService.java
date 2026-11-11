package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueStructureService {

    private final BranchCreationService branchCreationService =
            new BranchCreationService();

    private final AnswerCreationService answerCreationService =
            new AnswerCreationService();

    private final DialogueDeletionService deletionService =
            new DialogueDeletionService();

    private final DialogueRenameService dialogueRenameService =
            new DialogueRenameService();

    private final BranchRenameService branchRenameService =
            new BranchRenameService();

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

        if (dialogue.getBranches().get(branch.getId()) != branch) {
            throw new IllegalArgumentException(
                    "Гілка не належить цьому діалогу."
            );
        }

        if (targetBranchId != null
                && !targetBranchId.isBlank()
                && !dialogue.getBranches().containsKey(targetBranchId)) {
            throw new IllegalArgumentException(
                    "Цільову гілку не знайдено: " + targetBranchId
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

    public void renameDialogue(Dialogue dialogue, String newName) {
        dialogueRenameService.rename(dialogue, newName);
    }

    public void renameBranch(Branch branch, String newName) {
        branchRenameService.rename(branch, newName);
    }

    public void deleteBranch(Dialogue dialogue, String branchId) {
        deletionService.deleteBranch(dialogue, branchId);
    }

    public void deleteAnswer(Branch branch, String answerId) {
        deletionService.deleteAnswer(branch, answerId);
    }

    public void validate(Dialogue dialogue) {
        DialogueStructureValidator.validate(dialogue);
    }
}
