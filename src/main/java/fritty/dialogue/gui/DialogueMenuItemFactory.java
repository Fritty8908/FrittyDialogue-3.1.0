package fritty.dialogue.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DialogueMenuItemFactory {

    private DialogueMenuItemFactory() {
    }

    public static ItemStack create(
            Material material,
            String name,
            List<String> lore,
            boolean enchanted
    ) {
        Objects.requireNonNull(material, "Material cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(name);

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(new ArrayList<>(lore));
        }

        if (enchanted) {
            meta.setEnchantmentGlintOverride(true);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack create(
            Material material,
            String name,
            String... lore
    ) {
        return create(
                material,
                name,
                lore == null ? List.of() : List.of(lore),
                false
        );
    }

    public static ItemStack createButton(
            Material material,
            String name,
            String description
    ) {
        List<String> lore = description == null
                ? List.of()
                : List.of(description);

        return create(material, name, lore, false);
    }

    public static ItemStack createBackButton() {
        return createButton(
                Material.ARROW,
                "§eНазад",
                "§7Повернутися до попереднього меню"
        );
    }

    public static ItemStack createCloseButton() {
        return createButton(
                Material.BARRIER,
                "§cЗакрити",
                "§7Закрити меню"
        );
    }

    public static ItemStack createNextPageButton() {
        return createButton(
                Material.ARROW,
                "§aНаступна сторінка",
                "§7Перейти на наступну сторінку"
        );
    }

    public static ItemStack createPreviousPageButton() {
        return createButton(
                Material.ARROW,
                "§aПопередня сторінка",
                "§7Повернутися на попередню сторінку"
        );
    }

    public static ItemStack createCreateButton(
            String objectName
    ) {
        return createButton(
                Material.LIME_DYE,
                "§aСтворити " + objectName,
                "§7Додати новий елемент"
        );
    }

    public static ItemStack createDeleteButton(
            String objectName
    ) {
        return createButton(
                Material.BARRIER,
                "§cВидалити " + objectName,
                "§7Видалити цей елемент"
        );
    }

    public static ItemStack createSettingsButton() {
        return create(
                Material.COMPARATOR,
                "§eНалаштування",
                List.of("§7Відкрити параметри"),
                true
        );
    }

    public static ItemStack createDialogueItem(
            String dialogueName
    ) {
        return createButton(
                Material.WRITTEN_BOOK,
                "§f" + dialogueName,
                "§7Відкрити редактор діалогу"
        );
    }

    public static ItemStack createRootBranchItem(
            String branchName
    ) {
        return createButton(
                Material.ENCHANTED_BOOK,
                "§6" + branchName,
                "§7Коренева гілка діалогу"
        );
    }

    public static ItemStack createBranchItem(
            String branchName
    ) {
        return createButton(
                Material.PAPER,
                "§f" + branchName,
                "§7Відкрити редактор гілки"
        );
    }

    public static ItemStack createAnswerItem(
            String answerText
    ) {
        return createButton(
                Material.PAPER,
                "§f" + answerText,
                "§7Редагувати відповідь гравця"
        );
    }

    public static ItemStack createConditionItem(
            String conditionName
    ) {
        return createButton(
                Material.COMPARATOR,
                "§b" + conditionName,
                "§7Налаштувати умову"
        );
    }
}
