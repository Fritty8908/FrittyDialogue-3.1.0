package fritty.dialogue.gui;

import org.bukkit.inventory.Inventory;

import java.util.Objects;

public final class DialogueMenuActionSetup {

    private final DialogueMenuActionRegistry registry;

    public DialogueMenuActionSetup(
            DialogueMenuActionRegistry registry
    ) {
        this.registry = Objects.requireNonNull(
                registry,
                "Action registry cannot be null"
        );
    }

    public void registerDefaults() {
        registerNavigation();
        registerDialogueMenus();
        registerBranchMenus();
        registerAnswerMenus();
        registerConditionMenus();
        registerSettingsMenus();
    }

    private void registerNavigation() {
        for (DialogueMenuHolder.MenuType type
                : DialogueMenuHolder.MenuType.values()) {
            registry.register(type, 49, DialogueMenuAction.BACK);
            registry.register(type, 50, DialogueMenuAction.CLOSE);
        }

        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_LIST,
                48,
                DialogueMenuAction.PREVIOUS_PAGE
        );

        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_LIST,
                52,
                DialogueMenuAction.NEXT_PAGE
        );

        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_EDITOR,
                48,
                DialogueMenuAction.PREVIOUS_PAGE
        );

        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_EDITOR,
                52,
                DialogueMenuAction.NEXT_PAGE
        );

        registerFor(
                DialogueMenuHolder.MenuType.BRANCH_EDITOR,
                48,
                DialogueMenuAction.PREVIOUS_PAGE
        );

        registerFor(
                DialogueMenuHolder.MenuType.BRANCH_EDITOR,
                52,
                DialogueMenuAction.NEXT_PAGE
        );
    }

    private void registerDialogueMenus() {
        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_LIST,
                53,
                DialogueMenuAction.CREATE_DIALOGUE
        );

        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_EDITOR,
                45,
                DialogueMenuAction.OPEN_DIALOGUE_SETTINGS
        );

        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_EDITOR,
                53,
                DialogueMenuAction.CREATE_BRANCH
        );

        registerFor(
                DialogueMenuHolder.MenuType.DIALOGUE_EDITOR,
                46,
                DialogueMenuAction.DELETE_DIALOGUE
        );
    }

    private void registerBranchMenus() {
        registerFor(
                DialogueMenuHolder.MenuType.BRANCH_EDITOR,
                45,
                DialogueMenuAction.OPEN_BRANCH_SETTINGS
        );

        registerFor(
                DialogueMenuHolder.MenuType.BRANCH_EDITOR,
                53,
                DialogueMenuAction.CREATE_ANSWER
        );

        registerFor(
                DialogueMenuHolder.MenuType.BRANCH_SETTINGS,
                46,
                DialogueMenuAction.OPEN_COMMAND_SETTINGS
        );

        registerFor(
                DialogueMenuHolder.MenuType.BRANCH_SETTINGS,
                47,
                DialogueMenuAction.OPEN_LOCATION_SETTINGS
        );

        registerFor(
                DialogueMenuHolder.MenuType.BRANCH_SETTINGS,
                48,
                DialogueMenuAction.OPEN_CONDITIONS
        );
    }

    private void registerAnswerMenus() {
        registerFor(
                DialogueMenuHolder.MenuType.ANSWER_EDITOR,
                45,
                DialogueMenuAction.EDIT_ANSWER_TEXT
        );

        registerFor(
                DialogueMenuHolder.MenuType.ANSWER_EDITOR,
                46,
                DialogueMenuAction.EDIT_ANSWER_TARGET
        );

        registerFor(
                DialogueMenuHolder.MenuType.ANSWER_EDITOR,
                47,
                DialogueMenuAction.EDIT_ANSWER_COMMAND
        );

        registerFor(
                DialogueMenuHolder.MenuType.ANSWER_EDITOR,
                48,
                DialogueMenuAction.SELECT_AVAILABILITY_MODE
        );

        registerFor(
                DialogueMenuHolder.MenuType.ANSWER_EDITOR,
                53,
                DialogueMenuAction.DELETE_ANSWER
        );
    }

    private void registerConditionMenus() {
        registerFor(
                DialogueMenuHolder.MenuType.CONDITION_LIST,
                53,
                DialogueMenuAction.ADD_CONDITION
        );

        registerFor(
                DialogueMenuHolder.MenuType.CONDITION_LIST,
                45,
                DialogueMenuAction.TOGGLE_CONDITION_OPERATOR
        );

        registerFor(
                DialogueMenuHolder.MenuType.CONDITION_EDITOR,
                53,
                DialogueMenuAction.REMOVE_CONDITION
        );
    }

    private void registerSettingsMenus() {
        registerFor(
                DialogueMenuHolder.MenuType.COMMAND_SETTINGS,
                45,
                DialogueMenuAction.EDIT_COMMAND
        );

        registerFor(
                DialogueMenuHolder.MenuType.COMMAND_SETTINGS,
                47,
                DialogueMenuAction.SELECT_COMMAND_TIMING
        );

        registerFor(
                DialogueMenuHolder.MenuType.LOCATION_SETTINGS,
                45,
                DialogueMenuAction.CAPTURE_TEXT_LOCATION
        );

        registerFor(
                DialogueMenuHolder.MenuType.LOCATION_SETTINGS,
                46,
                DialogueMenuAction.CAPTURE_CAMERA_LOCATION
        );

        registerFor(
                DialogueMenuHolder.MenuType.LOCATION_SETTINGS,
                48,
                DialogueMenuAction.CLEAR_TEXT_LOCATION
        );

        registerFor(
                DialogueMenuHolder.MenuType.LOCATION_SETTINGS,
                49,
                DialogueMenuAction.CLEAR_CAMERA_LOCATION
        );
    }

    private void registerFor(
            DialogueMenuHolder.MenuType menuType,
            int slot,
            DialogueMenuAction action
    ) {
        registry.register(menuType, slot, action);
    }
}
