package fritty.dialogue.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public final class DialogueMenuFactory {

    private DialogueMenuFactory() {
    }

    public static Inventory create(
            DialogueMenuHolder.MenuType menuType,
            String title,
            int size
    ) {
        return create(
                menuType,
                title,
                size,
                null,
                null,
                null,
                0
        );
    }

    public static Inventory create(
            DialogueMenuHolder.MenuType menuType,
            String title,
            int size,
            String dialogueId,
            String branchId,
            String answerId,
            int page
    ) {
        Objects.requireNonNull(menuType, "Menu type cannot be null");
        Objects.requireNonNull(title, "Menu title cannot be null");

        if (size < 9 || size > 54 || size % 9 != 0) {
            throw new IllegalArgumentException(
                    "Inventory size must be between 9 and 54 "
                            + "and divisible by 9"
            );
        }

        DialogueMenuHolder holder = new DialogueMenuHolder(
                menuType,
                dialogueId,
                branchId,
                answerId,
                page
        );

        Inventory inventory = Bukkit.createInventory(
                holder,
                size,
                Component.text(title)
        );

        holder.attachInventory(inventory);

        return inventory;
    }

    public static ItemStack createItem(
            Material material,
            String name
    ) {
        return createItem(
                material,
                name,
                List.of(),
                false
        );
    }

    public static ItemStack createItem(
            Material material,
            String name,
            List<String> lore
    ) {
        return createItem(
                material,
                name,
                lore,
                false
        );
    }

    public static ItemStack createItem(
            Material material,
            String name,
            List<String> lore,
            boolean glowing
    ) {
        Objects.requireNonNull(material, "Material cannot be null");
        Objects.requireNonNull(name, "Item name cannot be null");
        Objects.requireNonNull(lore, "Lore cannot be null");

        if (material.isAir()) {
            throw new IllegalArgumentException(
                    "AIR cannot be used as a menu item"
            );
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.displayName(Component.text(name));

        if (!lore.isEmpty()) {
            meta.lore(
                    lore.stream()
                            .map(Component::text)
                            .toList()
            );
        }

        if (glowing) {
            meta.setEnchantmentGlintOverride(true);
        }

        item.setItemMeta(meta);

        return item;
    }

    public static void setItem(
            Inventory inventory,
            int slot,
            ItemStack item
    ) {
        Objects.requireNonNull(inventory, "Inventory cannot be null");
        Objects.requireNonNull(item, "Item cannot be null");

        if (slot < 0 || slot >= inventory.getSize()) {
            throw new IndexOutOfBoundsException(
                    "Slot is outside inventory bounds: " + slot
            );
        }

        inventory.setItem(slot, item);
    }

    public static void fillEmptySlots(
            Inventory inventory,
            ItemStack item
    ) {
        Objects.requireNonNull(inventory, "Inventory cannot be null");
        Objects.requireNonNull(item, "Item cannot be null");

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, item.clone());
            }
        }
    }
}
