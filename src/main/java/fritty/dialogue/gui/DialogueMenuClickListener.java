package fritty.dialogue.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class DialogueMenuClickListener implements Listener {

    private final DialogueMenuManager menuManager;
    private final DialogueMenuController controller;

    public DialogueMenuClickListener(
            DialogueMenuManager menuManager,
            DialogueMenuController controller
    ) {
        this.menuManager = Objects.requireNonNull(
                menuManager,
                "Menu manager cannot be null"
        );

        this.controller = Objects.requireNonNull(
                controller,
                "Menu controller cannot be null"
        );
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!menuManager.isDialogueMenu(event.getView().getTopInventory())) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() == null
                || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        Inventory inventory = event.getView().getTopInventory();
        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType().isAir()) {
            return;
        }

        DialogueMenuHolder holder = menuManager.getHolder(inventory);

        if (holder == null) {
            return;
        }

        handleClick(player, holder, event.getSlot(), event.isLeftClick(), event.isRightClick());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (menuManager.isDialogueMenu(event.getView().getTopInventory())) {
            event.setCancelled(true);
        }
    }

    private void handleClick(
            Player player,
            DialogueMenuHolder holder,
            int slot,
            boolean leftClick,
            boolean rightClick
    ) {
        switch (holder.getMenuType()) {
            case DIALOGUE_LIST -> handleDialogueList(player, holder, slot);
            case DIALOGUE_EDITOR -> handleDialogueEditor(player, holder, slot);
            case DIALOGUE_SETTINGS -> handleDialogueSettings(player, holder, slot);
            case BRANCH_LIST -> handleBranchList(player, holder, slot);
            case BRANCH_EDITOR -> handleBranchEditor(player, holder, slot);
            case BRANCH_SETTINGS -> handleBranchSettings(player, holder, slot);
            case ANSWER_EDITOR -> handleAnswerEditor(player, holder, slot);
            case ANSWER_SETTINGS -> handleAnswerSettings(player, holder, slot);
            case CONDITION_LIST -> handleConditionList(player, holder, slot);
            case CONDITION_EDITOR -> handleConditionEditor(player, holder, slot);
            case COMMAND_SETTINGS -> handleCommandSettings(player, holder, slot);
            case LOCATION_SETTINGS -> handleLocationSettings(player, holder, slot);
        }
    }

    private void handleDialogueList(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openMainMenu(player, holder.getPage());
    }

    private void handleDialogueEditor(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openDialogueEditor(
                player,
                holder.getDialogueId(),
                holder.getPage()
        );
    }

    private void handleDialogueSettings(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openDialogueSettings(
                player,
                holder.getDialogueId(),
                holder.getPage()
        );
    }

    private void handleBranchList(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openDialogueEditor(
                player,
                holder.getDialogueId(),
                holder.getPage()
        );
    }

    private void handleBranchEditor(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openBranchEditor(
                player,
                holder.getDialogueId(),
                holder.getBranchId(),
                holder.getPage()
        );
    }

    private void handleBranchSettings(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openBranchSettings(
                player,
                holder.getDialogueId(),
                holder.getBranchId(),
                holder.getPage()
        );
    }

    private void handleAnswerEditor(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openAnswerEditor(
                player,
                holder.getDialogueId(),
                holder.getBranchId(),
                holder.getAnswerId()
        );
    }

    private void handleAnswerSettings(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openAvailabilityMode(
                player,
                holder.getDialogueId(),
                holder.getBranchId(),
                holder.getAnswerId()
        );
    }

    private void handleConditionList(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openBranchEditor(
                player,
                holder.getDialogueId(),
                holder.getBranchId(),
                holder.getPage()
        );
    }

    private void handleConditionEditor(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openBranchEditor(
                player,
                holder.getDialogueId(),
                holder.getBranchId(),
                holder.getPage()
        );
    }

    private void handleCommandSettings(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openCommandSettings(
                player,
                holder.getDialogueId(),
                holder.getBranchId()
        );
    }

    private void handleLocationSettings(
            Player player,
            DialogueMenuHolder holder,
            int slot
    ) {
        controller.openLocationSettings(
                player,
                holder.getDialogueId(),
                holder.getBranchId()
        );
    }
}
