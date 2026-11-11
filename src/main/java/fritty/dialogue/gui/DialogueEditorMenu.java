package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Dialogue;
import fritty.dialogue.dialogue.Branch;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DialogueEditorMenu {

    private static final int INVENTORY_SIZE = 54;
    private static final int ENTRIES_PER_PAGE = 36;

    private DialogueEditorMenu() {
    }

    public static Inventory create(Dialogue dialogue, int page) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        List<Branch> branches = new ArrayList<>(
                dialogue.getBranches().values()
        );

        int totalPages = Math.max(
                1,
                (branches.size() + ENTRIES_PER_PAGE - 1)
                        / ENTRIES_PER_PAGE
        );

        int currentPage = Math.min(page, totalPages - 1);

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.DIALOGUE_EDITOR,
                "Діалог: " + dialogue.getName(),
                INVENTORY_SIZE,
                dialogue.getId(),
                null,
                null,
                currentPage
        );

        int start = currentPage * ENTRIES_PER_PAGE;
        int end = Math.min(
                start + ENTRIES_PER_PAGE,
                branches.size()
        );

        int slot = 9;

        for (int index = start; index < end; index++) {
            Branch branch = branches.get(index);

            Material material = branch.isRoot()
                    ? Material.BOOK
                    : Material.PAPER;

            String displayName = branch.getName() == null
                    || branch.getName().isBlank()
                    ? branch.getId()
                    : branch.getName();

            DialogueMenuFactory.setItem(
                    inventory,
                    slot++,
                    DialogueMenuFactory.createItem(
                            material,
                            displayName,
                            List.of(
                                    " ",
                                    "ID: " + branch.getId(),
                                    " ",
                                    "Натисніть, щоб редагувати"
                            ),
                            branch.isRoot()
                    )
            );
        }

        DialogueMenuFactory.setItem(
                inventory,
                0,
                DialogueMenuFactory.createItem(
                        Material.ENCHANTED_BOOK,
                        "Загальні налаштування",
                        List.of(
                                " ",
                                "Налаштування всього діалогу"
                        ),
                        true
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                4,
                DialogueMenuFactory.createItem(
                        Material.WRITABLE_BOOK,
                        "Створити гілку",
                        List.of(
                                " ",
                                "Створити кореневу гілку",
                                "або наступну гілку діалогу"
                        )
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                8,
                DialogueMenuFactory.createItem(
                        Material.BARRIER,
                        "Видалити діалог",
                        List.of(
                                " ",
                                "Видалити цей діалог"
                        )
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                45,
                DialogueMenuFactory.createItem(
                        Material.ARROW,
                        "Назад до списку діалогів"
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

        if (currentPage > 0) {
            DialogueMenuFactory.setItem(
                    inventory,
                    48,
                    DialogueMenuFactory.createItem(
                            Material.ARROW,
                            "Попередня сторінка"
                    )
            );
        }

        if (currentPage < totalPages - 1) {
            DialogueMenuFactory.setItem(
                    inventory,
                    50,
                    DialogueMenuFactory.createItem(
                            Material.ARROW,
                            "Наступна сторінка"
                    )
            );
        }

        return inventory;
    }
}
