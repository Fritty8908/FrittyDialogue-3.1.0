package fritty.dialogue.gui;

import fritty.dialogue.conditions.Condition;
import fritty.dialogue.conditions.ConditionGroup;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class ConditionMenu {

    private ConditionMenu() {
    }

    public static Inventory create(
            DialogueMenuHolder.MenuType menuType,
            String dialogueId,
            String branchId,
            String answerId,
            ConditionGroup conditions,
            int page
    ) {
        Objects.requireNonNull(menuType, "Menu type cannot be null");

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        Inventory inventory = DialogueMenuFactory.create(
                menuType,
                "Умови доступності",
                54,
                dialogueId,
                branchId,
                answerId,
                page
        );

        DialogueMenuFactory.setItem(
                inventory,
                4,
                DialogueMenuFactory.createItem(
                        Material.REPEATER,
                        "Група умов",
                        List.of(
                                " ",
                                "Налаштувати логіку AND / OR",
                                " ",
                                "Усі умови або хоча б одна"
                        ),
                        true
                )
        );

        if (conditions != null) {
            int slot = 10;

            for (Condition condition : conditions.getConditions()) {
                if (slot >= 44) {
                    break;
                }

                DialogueMenuFactory.setItem(
                        inventory,
                        slot++,
                        DialogueMenuFactory.createItem(
                                Material.PAPER,
                                condition.toString(),
                                List.of(
                                        " ",
                                        "Натисніть, щоб редагувати умову"
                                )
                        )
                );
            }
        }

        DialogueMenuFactory.setItem(
                inventory,
                46,
                DialogueMenuFactory.createItem(
                        Material.WRITABLE_BOOK,
                        "Додати умову",
                        List.of(
                                " ",
                                "Створити нову умову"
                        ),
                        true
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                49,
                DialogueMenuFactory.createItem(
                        Material.BARRIER,
                        "Назад"
                )
        );

        return inventory;
    }
}
