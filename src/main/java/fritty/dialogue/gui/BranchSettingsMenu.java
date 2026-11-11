package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class BranchSettingsMenu {

    private BranchSettingsMenu() {
    }

    public static Inventory create(
            Dialogue dialogue,
            Branch branch,
            int page
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        int currentPage = Math.min(page, 1);

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.BRANCH_SETTINGS,
                "Налаштування гілки: " + branch.getName(),
                54,
                dialogue.getId(),
                branch.getId(),
                null,
                currentPage
        );

        if (currentPage == 0) {
            addSetting(
                    inventory,
                    11,
                    Material.ENDER_PEARL,
                    "Позиція тексту",
                    "location"
            );

            addSetting(
                    inventory,
                    12,
                    Material.ENDER_EYE,
                    "Позиція гравця та напрямок",
                    "camera-location"
            );

            addSetting(
                    inventory,
                    13,
                    Material.CLOCK,
                    "Затримка символів",
                    "char-delay"
            );

            addSetting(
                    inventory,
                    14,
                    Material.NOTE_BLOCK,
                    "Звук символів",
                    "char-sound"
            );

            addSetting(
                    inventory,
                    15,
                    Material.NOTE_BLOCK,
                    "Висота звуку",
                    "char-pitch"
            );

            addSetting(
                    inventory,
                    20,
                    Material.NOTE_BLOCK,
                    "Гучність звуку",
                    "sound-volume"
            );

            addSetting(
                    inventory,
                    21,
                    Material.PAPER,
                    "Текст по символах",
                    "per-char"
            );

            addSetting(
                    inventory,
                    22,
                    Material.MAP,
                    "Ширина рядка",
                    "line-width"
            );

            addSetting(
                    inventory,
                    23,
                    Material.SPYGLASS,
                    "Масштаб тексту",
                    "text-scale"
            );

            DialogueMenuFactory.setItem(
                    inventory,
                    50,
                    DialogueMenuFactory.createItem(
                            Material.ARROW,
                            "Наступна сторінка"
                    )
            );
        } else {
            addSetting(
                    inventory,
                    11,
                    Material.GLASS,
                    "Колір фону",
                    "background-color"
            );

            addSetting(
                    inventory,
                    12,
                    Material.GRAY_STAINED_GLASS_PANE,
                    "Прозорість фону",
                    "background-alpha"
            );

            addSetting(
                    inventory,
                    13,
                    Material.POTION,
                    "Уповільнення",
                    "use-slowness"
            );

            addSetting(
                    inventory,
                    14,
                    Material.INVISIBILITY_SPLASH_POTION,
                    "Невидимість",
                    "make-invisible"
            );

            addSetting(
                    inventory,
                    15,
                    Material.COMPASS,
                    "Зберігати початкову позицію",
                    "save-start-location"
            );

            addSetting(
                    inventory,
                    20,
                    Material.ENDER_EYE,
                    "Плавне переміщення",
                    "smooth-teleport"
            );

            addSetting(
                    inventory,
                    21,
                    Material.BELL,
                    "Підказки на екрані",
                    "actionbar-hints"
            );

            DialogueMenuFactory.setItem(
                    inventory,
                    48,
                    DialogueMenuFactory.createItem(
                            Material.ARROW,
                            "Попередня сторінка"
                    )
            );
        }

        DialogueMenuFactory.setItem(
                inventory,
                45,
                DialogueMenuFactory.createItem(
                        Material.ARROW,
                        "Назад до редактора гілки"
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                49,
                DialogueMenuFactory.createItem(
                        Material.BARRIER,
                        "Закрити"
                )
        );

        return inventory;
    }

    private static void addSetting(
            Inventory inventory,
            int slot,
            Material material,
            String title,
            String settingKey
    ) {
        DialogueMenuFactory.setItem(
                inventory,
                slot,
                DialogueMenuFactory.createItem(
                        material,
                        title,
                        List.of(
                                " ",
                                "Налаштування: " + settingKey,
                                " ",
                                "Якщо значення не задане,",
                                "використовується налаштування діалогу",
                                " ",
                                "Натисніть, щоб змінити"
                        )
                )
        );
    }
}
