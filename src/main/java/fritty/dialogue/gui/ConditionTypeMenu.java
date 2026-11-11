package fritty.dialogue.gui;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

public final class ConditionTypeMenu {

    private ConditionTypeMenu() {
    }

    public static Inventory create(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.CONDITION_EDITOR,
                "Тип нової умови",
                54,
                dialogueId,
                branchId,
                answerId,
                0
        );

        addType(
                inventory,
                10,
                Material.BOOK,
                "Завершений діалог",
                "DIALOGUE_COMPLETED"
        );

        addType(
                inventory,
                12,
                Material.WRITTEN_BOOK,
                "Завершена гілка",
                "BRANCH_COMPLETED"
        );

        addType(
                inventory,
                14,
                Material.PAPER,
                "Обрана відповідь",
                "ANSWER_SELECTED"
        );

        addType(
                inventory,
                16,
                Material.NAME_TAG,
                "Дозвіл LuckPerms",
                "PERMISSION"
        );

        addType(
                inventory,
                29,
                Material.OAK_SIGN,
                "Тег гравця Minecraft",
                "PLAYER_TAG"
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

    private static void addType(
            Inventory inventory,
            int slot,
            Material material,
            String title,
            String typeId
    ) {
        DialogueMenuFactory.setItem(
                inventory,
                slot,
                DialogueMenuFactory.createItem(
                        material,
                        title,
                        List.of(
                                " ",
                                "Тип умови: " + typeId,
                                " ",
                                "Натисніть, щоб вибрати"
                        )
                )
        );
    }
}
