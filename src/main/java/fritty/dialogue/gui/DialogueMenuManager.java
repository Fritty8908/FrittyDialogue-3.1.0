package fritty.dialogue.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DialogueMenuManager {

    private final JavaPlugin plugin;

    public DialogueMenuManager(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(
                plugin,
                "Plugin cannot be null"
        );
    }

    public void open(Player player, Inventory inventory) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(inventory, "Inventory cannot be null");

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof DialogueMenuHolder)) {
            throw new IllegalArgumentException(
                    "Inventory does not belong to FrittyDialogue"
            );
        }

        if (!player.isOnline()) {
            return;
        }

        player.openInventory(inventory);
    }

    public void close(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        if (player.isOnline()) {
            player.closeInventory();
        }
    }

    public boolean isDialogueMenu(Inventory inventory) {
        if (inventory == null) {
            return false;
        }

        return inventory.getHolder() instanceof DialogueMenuHolder;
    }

    public DialogueMenuHolder getHolder(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof DialogueMenuHolder dialogueHolder) {
            return dialogueHolder;
        }

        return null;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
