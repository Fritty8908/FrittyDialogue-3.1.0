package fritty.dialogue.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public final class DialogueMenuConfirmationListener implements Listener {

    private final DialogueMenuManager menuManager;

    public DialogueMenuConfirmationListener(
            DialogueMenuManager menuManager
    ) {
        this.menuManager = Objects.requireNonNull(
                menuManager,
                "Menu manager cannot be null"
        );
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getView().getTopInventory();

        if (!menuManager.isDialogueMenu(inventory)) {
            return;
        }

        DialogueMenuHolder holder = menuManager.getHolder(inventory);

        if (holder == null
                || holder.getMenuType()
                != DialogueMenuHolder.MenuType.DIALOGUE_SETTINGS) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() != inventory) {
            return;
        }

        switch (event.getRawSlot()) {
            case 11 -> {
                menuManager.completeConfirmation(player, true);
                player.closeInventory();
            }
            case 15 -> {
                menuManager.completeConfirmation(player, false);
                player.closeInventory();
            }
            default -> {
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getInventory();

        if (!menuManager.isDialogueMenu(inventory)) {
            return;
        }

        DialogueMenuHolder holder = menuManager.getHolder(inventory);

        if (holder == null
                || holder.getMenuType()
                != DialogueMenuHolder.MenuType.DIALOGUE_SETTINGS) {
            return;
        }

        menuManager.cancelConfirmation(player);
    }
}
