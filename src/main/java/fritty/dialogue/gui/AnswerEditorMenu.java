package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class AnswerEditorMenu {

    private AnswerEditorMenu() {
    }

    public static Inventory create(
            Dialogue dialogue,
            Branch branch,
            Answer answer
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(branch, "Branch cannot be null");
        Objects.requireNonNull(answer, "Answer cannot be null");

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.ANSWER_EDITOR,
                "Редактор відповіді",
                54,
                dialogue.getId(),
                branch.getId(),
                answer.getId(),
                0
        );

        DialogueMenuFactory.setItem(
                inventory,
                4,
                DialogueMenuFactory.createItem(
                        Material.PAPER,
                        "Текст відповіді",
                        List.of(
                                " ",
                                answer.getText(),
                                " ",
                                "Натисніть, щоб змінити текст"
                        ),
                        true
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                19,
                DialogueMenuFactory.createItem(
                        Material.COMPASS,
                        "Перехід до гілки",
                        List.of(
                                " ",
                                answer.getTargetBranchId() == null
                                        ? "Не задано"
                                        : answer.getTargetBranchId(),
                                " ",
                                "Налаштувати цільову гілку"
                        )
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                21,
                DialogueMenuFactory.createItem(
                        Material.COMMAND_BLOCK,
                        "Команда відповіді",
                        List.of(
                                " ",
                                answer.getCommand() == null
                                        || answer.getCommand().isBlank()
                                        ? "Не задано"
                                        : answer.getCommand(),
                                " ",
                                "Команда виконується від імені консолі"
                        )
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                23,
                DialogueMenuFactory.createItem(
                        Material.REPEATER,
                        "Умови доступності",
                        List.of(
                                " ",
                                "Налаштувати умови показу відповіді"
                        )
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                25,
                DialogueMenuFactory.createItem(
                        Material.CLOCK,
                        "Одноразова відповідь",
                        List.of(
                                " ",
                                "Поточне значення: "
                                        + (answer.isOneTime() ? "Так" : "Ні"),
                                " ",
                                "Натисніть, щоб перемкнути"
                        ),
                        answer.isOneTime()
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                40,
                DialogueMenuFactory.createItem(
                        Material.BARRIER,
                        "Видалити відповідь",
                        List.of(
                                " ",
                                "Видалити цей варіант відповіді"
                        )
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                45,
                DialogueMenuFactory.createItem(
                        Material.ARROW,
                        "Назад до гілки"
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
}
