package fritty.dialogue.gui;

import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueMenuNavigation {

    private final DialogueMenuController controller;

    public DialogueMenuNavigation(
            DialogueMenuController controller
    ) {
        this.controller = Objects.requireNonNull(
                controller,
                "Menu controller cannot be null"
        );
    }

    public void backToMain(Player player) {
        controller.openMainMenu(player, 0);
    }

    public void backToDialogue(
            Player player,
            String dialogueId
    ) {
        controller.openDialogueEditor(
                player,
                dialogueId,
                0
        );
    }

    public void backToBranch(
            Player player,
            String dialogueId,
            String branchId
    ) {
        controller.openBranchEditor(
                player,
                dialogueId,
                branchId,
                0
        );
    }

    public void openDialogueSettings(
            Player player,
            String dialogueId
    ) {
        controller.openDialogueSettings(
                player,
                dialogueId,
                0
        );
    }

    public void openBranchSettings(
            Player player,
            String dialogueId,
            String branchId
    ) {
        controller.openBranchSettings(
                player,
                dialogueId,
                branchId,
                0
        );
    }

    public void openAnswer(
            Player player,
            String dialogueId,
            String branchId,
            String answerId
    ) {
        controller.openAnswerEditor(
                player,
                dialogueId,
                branchId,
                answerId
        );
    }

    public void openCommandSettings(
            Player player,
            String dialogueId,
            String branchId
    ) {
        controller.openCommandSettings(
                player,
                dialogueId,
                branchId
        );
    }

    public void openLocationSettings(
            Player player,
            String dialogueId,
            String branchId
    ) {
        controller.openLocationSettings(
                player,
                dialogueId,
                branchId
        );
    }
}
