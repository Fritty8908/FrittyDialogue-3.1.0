package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class MainMenu {

    private static final int INVENTORY_SIZE = 54;
    private static final int ENTRIES_PER_PAGE = 45;

    private static final int CREATE_SLOT = 45;
    private static final int PREVIOUS_SLOT = 48;
    private static final int CLOSE_SLOT = 49;
    private static final int NEXT_SLOT = 50;

    private MainMenu() {
    }

    public static Inventory create(
            Collection<Dialogue> dialogues,
            int page
    ) {
        Objects.requireNonNull(
                dialogues,
                "Dialogues cannot be null"
        );

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        List<Dialogue> entries = dialogues.stream()
                .filter(Objects::nonNull)
                .toList();

        int totalPages = Math.max(
                1,
                (entries.size() + ENTRIES_PER_PAGE - 1)
                        / ENTRIES_PER_PAGE
        );

        int currentPage = Math.min(page, totalPages - 1);

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.DIALOGUE_LIST,
                "Редактор діалогів",
                INVENTORY_SIZE,
                null,
                null,
                null,
                currentPage
        );

        int start = currentPage * ENTRIES_PER_PAGE;
        int end = Math.min(
                start + ENTRIES_PER_PAGE,
                entries.size()
        );

        int slot = 0;

        for (int index = start; index < end; index++) {
            Dialogue dialogue = entries.get(index);

            ItemStack item = DialogueMenuFactory.createItem(
                    Material.BOOK,
                    dialogue.getName(),
                    List.of(
                            " ",
                            "ID: " + dialogue.getId(),
                            " ",
                            "Натисніть, щоб відкрити редактор"
                    )
            );

            DialogueMenuFactory.setItem(
                    inventory,
                    slot++,
                    item
            );
        }

        DialogueMenuFactory.setItem(
                inventory,
                CREATE_SLOT,
                DialogueMenuFactory.createItem(
                        Material.WRITABLE_BOOK,
                        "Створити діалог",
                        List.of(
                                " ",
                                "Створити новий діалог"
                        ),
                        true
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                CLOSE_SLOT,
                DialogueMenuFactory.createItem(
                        Material.BARRIER,
                        "Закрити"
                )
        );

        if (currentPage > 0) {
            DialogueMenuFactory.setItem(
                    inventory,
                    PREVIOUS_SLOT,
                    DialogueMenuFactory.createItem(
                            Material.ARROW,
                            "Попередня сторінка",
                            List.of(
                                    "Сторінка: " + currentPage
                            )
                    )
            );
        }

        if (currentPage < totalPages - 1) {
            DialogueMenuFactory.setItem(
                    inventory,
                    NEXT_SLOT,
                    DialogueMenuFactory.createItem(
                            Material.ARROW,
                            "Наступна сторінка",
                            List.of(
                                    "Сторінка: " + (currentPage + 2)
                            )
                    )
            );
        }

        return inventory;
    }
}
