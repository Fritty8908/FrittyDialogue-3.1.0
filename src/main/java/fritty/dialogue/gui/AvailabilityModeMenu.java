package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Answer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class AvailabilityModeMenu {

    private AvailabilityModeMenu() {
    }

    public static Inventory create(
            String dialogueId,
            String branchId,
            Answer answer
    ) {
        Objects.requireNonNull(answer, "Answer cannot be null");

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.ANSWER_SETTINGS,
                "Відображення відповіді",
                54,
                dialogueId,
                branchId,
                answer.getId(),
                0
        );

        addMode(
                inventory,
                20,
                Material.BARRIER,
                "Приховувати",
                Answer.AvailabilityMode.HIDE,
                answer
        );

        addMode(
                inventory,
                22,
                Material.GRAY_DYE,
                "Показувати неактивною",
                Answer.AvailabilityMode.DISABLE,
                answer
        );

        addMode(
                inventory,
                24,
                Material.LIME_DYE,
                "Залишати доступною",
                Answer.AvailabilityMode.AVAILABLE,
                answer
        );

        DialogueMenuFactory.setItem(
                inventory,
                45,
                DialogueMenuFactory.createItem(
                        Material.ARROW,
                        "Назад до редактора відповіді"
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

    private static void addMode(
            Inventory inventory,
            int slot,
            Material material,
            String title,
            Answer.AvailabilityMode mode,
            Answer answer
    ) {
        DialogueMenuFactory.setItem(
                inventory,
                slot,
                DialogueMenuFactory.createItem(
                        material,
                        title,
                        List.of(
                                " ",
                                switch (mode) {
                                    case HIDE ->
                                            "Відповідь не показується гравцю";
                                    case DISABLE ->
                                            "Відповідь видима, але недоступна";
                                    case AVAILABLE ->
                                            "Відповідь залишається доступною";
                                },
                                " ",
                                answer.getAvailabilityMode() == mode
                                        ? "Поточний режим"
                                        : "Натисніть, щоб вибрати"
                        ),
                        answer.getAvailabilityMode() == mode
                )
        );
    }
}
