package fritty.dialogue.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public final class DialogueMenuActionListener implements Listener {

    private final DialogueMenuManager menuManager;
    private final DialogueMenuActionDispatcher dispatcher;

    public DialogueMenuActionListener(
            DialogueMenuManager menuManager,
            DialogueMenuActionDispatcher dispatcher
    ) {
        this.menuManager = Objects.requireNonNull(menuManager);
        this.dispatcher = Objects.requireNonNull(dispatcher);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        Inventory inventory = event.getView().getTopInventory();

        if (!menuManager.isDialogueMenu(inventory)) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory() != inventory) {
            return;
        }

        DialogueMenuHolder holder = menuManager.getHolder(inventory);

        if (holder == null) {
            return;
        }

        dispatcher.dispatch(
                player,
                holder,
                event.getSlot()
        );
    }
}
