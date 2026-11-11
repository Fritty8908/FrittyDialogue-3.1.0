package fritty.dialogue.gui;

import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueMenuActionDispatcher {

    private final DialogueMenuActionRegistry registry;
    private final DialogueMenuController controller;
    private final DialogueMenuNavigation navigation;

    public DialogueMenuActionDispatcher(
            DialogueMenuActionRegistry registry,
            DialogueMenuController controller,
            DialogueMenuNavigation navigation
    ) {
        this.registry = Objects.requireNonNull(registry);
        this.controller = Objects.requireNonNull(controller);
        this.navigation = Objects.requireNonNull(navigation);
    }

    public void dispatch(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        if (player == null || !player.isOnline() || holder == null) {
            return;
        }

        DialogueMenuAction action = registry.getAction(
                holder.getMenuType(),
                slot
        );

        switch (action) {
            case NONE -> {
            }

            case BACK -> handleBack(player, holder);

            case CLOSE -> player.closeInventory();

            case PREVIOUS_PAGE -> handlePreviousPage(player, holder);

            case NEXT_PAGE -> handleNextPage(player, holder);

            case OPEN_DIALOGUE -> controller.openDialogueEditor(
                    player,
                    holder.getDialogueId(),
                    holder.getPage()
            );

            case OPEN_DIALOGUE_SETTINGS -> navigation.openDialogueSettings(
                    player,
                    holder.getDialogueId()
            );

            case OPEN_BRANCH -> controller.openBranchEditor(
                    player,
                    holder.getDialogueId(),
                    holder.getBranchId(),
                    0
            );

            case OPEN_BRANCH_SETTINGS -> navigation.openBranchSettings(
                    player,
                    holder.getDialogueId(),
                    holder.getBranchId()
            );

            case OPEN_ANSWER -> navigation.openAnswer(
                    player,
                    holder.getDialogueId(),
                    holder.getBranchId(),
                    holder.getAnswerId()
            );

            case OPEN_COMMAND_SETTINGS -> navigation.openCommandSettings(
                    player,
                    holder.getDialogueId(),
                    holder.getBranchId()
            );

            case OPEN_LOCATION_SETTINGS -> navigation.openLocationSettings(
                    player,
                    holder.getDialogueId(),
                    holder.getBranchId()
            );

            default -> {
                // Інші дії обробляються відповідними редакторами.
            }
        }
    }

    private void handleBack(
            Player player,
            DialogueMenuHolder holder
    ) {
        switch (holder.getMenuType()) {
            case DIALOGUE_LIST -> player.closeInventory();

            case DIALOGUE_EDITOR, DIALOGUE_SETTINGS ->
                    navigation.backToMain(player);

            case BRANCH_LIST ->
                    navigation.backToDialogue(
                            player,
                            holder.getDialogueId()
                    );

            case BRANCH_EDITOR, BRANCH_SETTINGS ->
                    navigation.backToDialogue(
                            player,
                            holder.getDialogueId()
                    );

            case ANSWER_EDITOR, ANSWER_SETTINGS ->
                    navigation.backToBranch(
                            player,
                            holder.getDialogueId(),
                            holder.getBranchId()
                    );

            case CONDITION_LIST, CONDITION_EDITOR,
                    COMMAND_SETTINGS, LOCATION_SETTINGS ->
                    navigation.backToBranch(
                            player,
                            holder.getDialogueId(),
                            holder.getBranchId()
                    );
        }
    }

    private void handlePreviousPage(
            Player player,
            DialogueMenuHolder holder
    ) {
        int page = Math.max(0, holder.getPage() - 1);

        switch (holder.getMenuType()) {
            case DIALOGUE_LIST -> controller.openMainMenu(player, page);

            case DIALOGUE_EDITOR ->
                    controller.openDialogueEditor(
                            player,
                            holder.getDialogueId(),
                            page
                    );

            case BRANCH_EDITOR ->
                    controller.openBranchEditor(
                            player,
                            holder.getDialogueId(),
                            holder.getBranchId(),
                            page
                    );

            default -> {
            }
        }
    }

    private void handleNextPage(
            Player player,
            DialogueMenuHolder holder
    ) {
        int page = holder.getPage() + 1;

        switch (holder.getMenuType()) {
            case DIALOGUE_LIST -> controller.openMainMenu(player, page);

            case DIALOGUE_EDITOR ->
                    controller.openDialogueEditor(
                            player,
                            holder.getDialogueId(),
                            page
                    );

            case BRANCH_EDITOR ->
                    controller.openBranchEditor(
                            player,
                            holder.getDialogueId(),
                            holder.getBranchId(),
                            page
                    );

            default -> {
            }
        }
    }
}
