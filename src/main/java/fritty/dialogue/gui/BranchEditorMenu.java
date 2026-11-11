package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class BranchEditorMenu {

    private static final int INVENTORY_SIZE = 54;
    private static final int ANSWERS_PER_PAGE = 36;

    private BranchEditorMenu() {
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

        List<Answer> answers = branch.getAnswers();

        int totalPages = Math.max(
                1,
                (answers.size() + ANSWERS_PER_PAGE - 1)
                        / ANSWERS_PER_PAGE
        );

        int currentPage = Math.min(page, totalPages - 1);

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.BRANCH_EDITOR,
                "Гілка: " + branch.getName(),
                INVENTORY_SIZE,
                dialogue.getId(),
                branch.getId(),
                null,
                currentPage
        );

        DialogueMenuFactory.setItem(
                inventory,
                4,
                DialogueMenuFactory.createItem(
                        Material.BOOK,
                        "Текст NPC",
                        List.of(
                                " ",
                                "Переглянути або змінити текст NPC",
                                "Налаштувати параметри цієї гілки"
                        ),
                        true
                )
        );

        int start = currentPage * ANSWERS_PER_PAGE;
        int end = Math.min(
                start + ANSWERS_PER_PAGE,
                answers.size()
        );

        int slot = 9;

        for (int index = start; index < end; index++) {
            Answer answer = answers.get(index);

            DialogueMenuFactory.setItem(
                    inventory,
                    slot++,
                    DialogueMenuFactory.createItem(
                            Material.PAPER,
                            answer.getText(),
                            List.of(
                                    " ",
                                    "ID: " + answer.getId(),
                                    " ",
                                    "Натисніть, щоб редагувати відповідь"
                            )
                    )
            );
        }

        DialogueMenuFactory.setItem(
                inventory,
                0,
                DialogueMenuFactory.createItem(
                        Material.ARROW,
                        "Назад до списку гілок"
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                8,
                DialogueMenuFactory.createItem(
                        Material.WRITABLE_BOOK,
                        "Додати відповідь",
                        List.of(
                                " ",
                                "Створити новий варіант відповіді"
                        ),
                        true
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
