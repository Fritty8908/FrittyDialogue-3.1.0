package fritty.dialogue.gui;

import fritty.dialogue.conditions.ConditionGroup;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class ConditionGroupMenu {

    private ConditionGroupMenu() {
    }

    public static Inventory create(
            String dialogueId,
            String branchId,
            String answerId,
            ConditionGroup conditions
    ) {
        Objects.requireNonNull(
                conditions,
                "Condition group cannot be null"
        );

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.CONDITION_LIST,
                "Логіка групи умов",
                54,
                dialogueId,
                branchId,
                answerId,
                0
        );

        DialogueMenuFactory.setItem(
                inventory,
                20,
                DialogueMenuFactory.createItem(
                        Material.REPEATER,
                        "AND — усі умови",
                        List.of(
                                " ",
                                "Доступ дозволено, якщо виконані",
                                "абсолютно всі умови групи",
                                " ",
                                "Натисніть, щоб вибрати"
                        ),
                        conditions.getOperator()
                                == ConditionGroup.Operator.AND
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                24,
                DialogueMenuFactory.createItem(
                        Material.COMPARATOR,
                        "OR — хоча б одна умова",
                        List.of(
                                " ",
                                "Доступ дозволено, якщо виконана",
                                "хоча б одна умова групи",
                                " ",
                                "Натисніть, щоб вибрати"
                        ),
                        conditions.getOperator()
                                == ConditionGroup.Operator.OR
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                45,
                DialogueMenuFactory.createItem(
                        Material.ARROW,
                        "Назад до умов"
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
