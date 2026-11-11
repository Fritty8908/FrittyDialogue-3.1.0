package fritty.dialogue.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Objects;
import java.util.function.BooleanConsumer;

public final class DialogueMenuConfirmation {

    private final DialogueMenuManager menuManager;

    public DialogueMenuConfirmation(
            DialogueMenuManager menuManager
    ) {
        this.menuManager = Objects.requireNonNull(
                menuManager,
                "Menu manager cannot be null"
        );
    }

    public void open(
            Player player,
            String title,
            String description,
            java.util.function.Consumer<Boolean> callback
    ) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(callback, "Callback cannot be null");

        Inventory inventory = menuManager.createMenu(
                player,
                title,
                27,
                DialogueMenuHolder.MenuType.DIALOGUE_SETTINGS
        );

        inventory.setItem(
                11,
                DialogueMenuItemFactory.createButton(
                        Material.LIME_CONCRETE,
                        "§aПідтвердити",
                        description
                )
        );

        inventory.setItem(
                15,
                DialogueMenuItemFactory.createButton(
                        Material.RED_CONCRETE,
                        "§cСкасувати",
                        "§7Повернутися без змін"
                )
        );

        player.openInventory(inventory);

        menuManager.setConfirmationCallback(
                player,
                callback
        );
    }
}
