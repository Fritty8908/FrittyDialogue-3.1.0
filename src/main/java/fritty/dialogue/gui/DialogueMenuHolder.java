package fritty.dialogue.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

public final class DialogueMenuHolder implements InventoryHolder {

    private final MenuType menuType;

    private final String dialogueId;
    private final String branchId;
    private final String answerId;

    private final int page;

    private Inventory inventory;

    public DialogueMenuHolder(
            MenuType menuType,
            String dialogueId,
            String branchId,
            String answerId,
            int page
    ) {
        this.menuType = Objects.requireNonNull(
                menuType,
                "Menu type cannot be null"
        );

        this.dialogueId = dialogueId;
        this.branchId = branchId;
        this.answerId = answerId;

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        this.page = page;
    }

    public DialogueMenuHolder(
            MenuType menuType,
            String dialogueId
    ) {
        this(
                menuType,
                dialogueId,
                null,
                null,
                0
        );
    }

    public DialogueMenuHolder(MenuType menuType) {
        this(
                menuType,
                null,
                null,
                null,
                0
        );
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public String getDialogueId() {
        return dialogueId;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public int getPage() {
        return page;
    }

    public boolean hasDialogue() {
        return dialogueId != null
                && !dialogueId.isBlank();
    }

    public boolean hasBranch() {
        return branchId != null
                && !branchId.isBlank();
    }

    public boolean hasAnswer() {
        return answerId != null
                && !answerId.isBlank();
    }

    public void attachInventory(Inventory inventory) {
        Objects.requireNonNull(
                inventory,
                "Inventory cannot be null"
        );

        if (this.inventory != null) {
            throw new IllegalStateException(
                    "Inventory is already attached"
            );
        }

        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public enum MenuType {

        DIALOGUE_LIST,

        DIALOGUE_EDITOR,
        DIALOGUE_SETTINGS,

        BRANCH_LIST,
        BRANCH_EDITOR,
        BRANCH_SETTINGS,

        ANSWER_EDITOR,
        ANSWER_SETTINGS,

        CONDITION_LIST,
        CONDITION_EDITOR,

        COMMAND_SETTINGS,

        LOCATION_SETTINGS
    }
}
