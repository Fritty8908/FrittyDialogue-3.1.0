package fritty.dialogue.gui;

import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Objects;

public final class LocationSettingsMenu {

    private LocationSettingsMenu() {
    }

    public static Inventory create(
            Dialogue dialogue,
            Branch branch
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        Inventory inventory = DialogueMenuFactory.create(
                DialogueMenuHolder.MenuType.LOCATION_SETTINGS,
                branch == null
                        ? "Позиції діалогу"
                        : "Позиції гілки: " + branch.getName(),
                54,
                dialogue.getId(),
                branch == null ? null : branch.getId(),
                null,
                0
        );

        DialogueMenuFactory.setItem(
                inventory,
                20,
                DialogueMenuFactory.createItem(
                        Material.ENDER_PEARL,
                        "Позиція тексту",
                        List.of(
                                " ",
                                "Зберегти позицію тексту діалогу",
                                " ",
                                "Позиція визначається на рівні очей",
                                " ",
                                "Натисніть, щоб захопити поточну позицію"
                        ),
                        true
                )
        );

        DialogueMenuFactory.setItem(
                inventory,
                24,
                DialogueMenuFactory.createItem(
                        Material.ENDER_EYE,
                        "Позиція гравця та напрямок",
                        List.of(
                                " ",
                                "Зберегти позицію ніг гравця",
                                "Разом із напрямком yaw та pitch",
                                " ",
                                "Висота зберігається без зміщення",
                                " ",
                                "Натисніть, щоб захопити поточну позицію"
                        ),
                        true
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
