package fritty.dialogue.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public final class DialogueMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!isDialogueMenu(topInventory)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();

        if (!isDialogueMenu(topInventory)) {
            return;
        }

        int topSize = topInventory.getSize();

        boolean affectsMenu = event.getRawSlots()
                .stream()
                .anyMatch(slot -> slot < topSize);

        if (affectsMenu) {
            event.setCancelled(true);
        }
    }

    private boolean isDialogueMenu(Inventory inventory) {
        return inventory.getHolder() instanceof DialogueMenuHolder;
    }
}
