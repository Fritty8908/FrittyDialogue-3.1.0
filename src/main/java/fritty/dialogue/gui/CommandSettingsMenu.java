package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class CommandSettingsMenu {

    private CommandSettingsMenu() {
    }

    public static Inventory create(
            Dialogue dialogue,
            Branch branch
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");
        Objects.requireNonNull(branch, "Branch cannot be null");

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.COMMAND_SETTINGS,
                "Команда гілки: " + branch.getName(),
                54,
                dialogue.getId(),
                branch.getId(),
                null,
                0
        );

        String command = branch.getCommand();

        DialogueMenuFactory.setItem(
                inventory,
                13,
                DialogueMenuFactory.createItem(
                        Material.COMMAND_BLOCK,
                        "Команда",
                        List.of(
                                " ",
                                command == null || command.isBlank()
                                        ? "Команду не задано"
                                        : command,
                                " ",
                                "Натисніть, щоб ввести або змінити команду"
                        ),
                        true
                )
        );

        Branch.CommandTiming timing = branch.getCommandTiming();

        DialogueMenuFactory.setItem(
                inventory,
                29,
                DialogueMenuFactory.createItem(
                        Material.LIGHTNING_ROD,
                        "Виконання на початку",
                        List.of(
                                " ",
                                "Команда виконується одразу",
                                "після початку гілки",
                                " ",
                                "Поточний режим: "
                                        + (timing == Branch.CommandTiming.ON_START
                                        ? "Так" : "Ні")
                        ),
                        timing == Branch.CommandTiming.ON_START
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                31,
                DialogueMenuFactory.createItem(
                        Material.CLOCK,
                        "Виконання після тексту",
                        List.of(
                                " ",
                                "Команда виконується після",
                                "повного відображення тексту NPC",
                                " ",
                                "Поточний режим: "
                                        + (timing == Branch.CommandTiming.AFTER_TEXT
                                        ? "Так" : "Ні")
                        ),
                        timing == Branch.CommandTiming.AFTER_TEXT
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                33,
                DialogueMenuFactory.createItem(
                        Material.BARRIER,
                        "Не виконувати команду",
                        List.of(
                                " ",
                                "Зберегти команду, але не запускати її",
                                " ",
                                "Поточний режим: "
                                        + (timing == Branch.CommandTiming.NONE
                                        ? "Так" : "Ні")
                        ),
                        timing == Branch.CommandTiming.NONE
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                45,
                DialogueMenuFactory.createItem(
                        Material.ARROW,
                        "Назад"
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
