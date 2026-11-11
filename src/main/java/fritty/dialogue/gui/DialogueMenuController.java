package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import fritty.dialogue.dialogue.DialogueManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public final class DialogueMenuController {

    private final DialogueManager dialogueManager;
    private final DialogueMenuManager menuManager;

    public DialogueMenuController(
            DialogueManager dialogueManager,
            DialogueMenuManager menuManager
    ) {
        this.dialogueManager = Objects.requireNonNull(
                dialogueManager,
                "Dialogue manager cannot be null"
        );

        this.menuManager = Objects.requireNonNull(
                menuManager,
                "Menu manager cannot be null"
        );
    }

    public void openMainMenu(Player player, int page) {
        open(
                player,
                MainMenu.create(
                        dialogueManager.getAll(),
                        page
                )
        );
    }

    public void openDialogueEditor(Player player, String dialogueId, int page) {
        Dialogue dialogue = getDialogue(dialogueId);

        if (dialogue == null) {
            return;
        }

        open(player, DialogueEditorMenu.create(dialogue, page));
    }

    public void openDialogueSettings(
            Player player,
            String dialogueId,
            int page
    ) {
        Dialogue dialogue = getDialogue(dialogueId);

        if (dialogue == null) {
            return;
        }

        open(player, DialogueSettingsMenu.create(dialogue, page));
    }

    public void openBranchEditor(
            Player player,
            String dialogueId,
            String branchId,
            int page
    ) {
        BranchContext context = getBranch(dialogueId, branchId);

        if (context == null) {
            return;
        }

        open(
                player,
                BranchEditorMenu.create(
                        context.dialogue(),
                        context.branch(),
                        page
                )
        );
    }

    public void openBranchSettings(
            Player player,
            String dialogueId,
            String branchId,
            int page
    ) {
        BranchContext context = getBranch(dialogueId, branchId);

        if (context == null) {
            return;
        }

        open(
                player,
                BranchSettingsMenu.create(
                        context.dialogue(),
                        context.branch(),
                        page
                )
        );
    }

    public void openAnswerEditor(
            Player player,
            String dialogueId,
            String branchId,
            String answerId
    ) {
        AnswerContext context = getAnswer(
                dialogueId,
                branchId,
                answerId
        );

        if (context == null) {
            return;
        }

        open(
                player,
                AnswerEditorMenu.create(
                        context.dialogue(),
                        context.branch(),
                        context.answer()
                )
        );
    }

    public void openAvailabilityMode(
            Player player,
            String dialogueId,
            String branchId,
            String answerId
    ) {
        AnswerContext context = getAnswer(
                dialogueId,
                branchId,
                answerId
        );

        if (context == null) {
            return;
        }

        open(
                player,
                AvailabilityModeMenu.create(
                        dialogueId,
                        branchId,
                        context.answer()
                )
        );
    }

    public void openCommandSettings(
            Player player,
            String dialogueId,
            String branchId
    ) {
        BranchContext context = getBranch(dialogueId, branchId);

        if (context == null) {
            return;
        }

        open(
                player,
                CommandSettingsMenu.create(
                        context.dialogue(),
                        context.branch()
                )
        );
    }

    public void openLocationSettings(
            Player player,
            String dialogueId,
            String branchId
    ) {
        Dialogue dialogue = getDialogue(dialogueId);

        if (dialogue == null) {
            return;
        }

        Branch branch = branchId == null
                ? null
                : dialogue.getBranches().get(branchId);

        if (branchId != null && branch == null) {
            return;
        }

        open(
                player,
                LocationSettingsMenu.create(
                        dialogue,
                        branch
                )
        );
    }

    private Dialogue getDialogue(String dialogueId) {
        if (dialogueId == null || dialogueId.isBlank()) {
            return null;
        }

        return dialogueManager.get(dialogueId);
    }

    private BranchContext getBranch(
            String dialogueId,
            String branchId
    ) {
        Dialogue dialogue = getDialogue(dialogueId);

        if (dialogue == null || branchId == null) {
            return null;
        }

        Branch branch = dialogue.getBranches().get(branchId);

        if (branch == null) {
            return null;
        }

        return new BranchContext(dialogue, branch);
    }

    private AnswerContext getAnswer(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        BranchContext context = getBranch(dialogueId, branchId);

        if (context == null || answerId == null) {
            return null;
        }

        Answer answer = context.branch()
                .getAnswers()
                .stream()
                .filter(candidate -> candidate.getId().equals(answerId))
                .findFirst()
                .orElse(null);

        if (answer == null) {
            return null;
        }

        return new AnswerContext(
                context.dialogue(),
                context.branch(),
                answer
        );
    }

    private void open(Player player, Inventory inventory) {
        if (player == null || !player.isOnline()) {
            return;
        }

        menuManager.open(player, inventory);
    }

    private record BranchContext(
            Dialogue dialogue,
            Branch branch
    ) {
    }

    private record AnswerContext(
            Dialogue dialogue,
            Branch branch,
            Answer answer
    ) {
    }
}
