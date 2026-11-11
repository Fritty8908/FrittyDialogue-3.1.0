package fritty.dialogue.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DialogueMenuActionRegistry {

    private final Map<String, DialogueMenuAction> actions =
            new HashMap<>();

    public void register(
            DialogueMenuHolder.MenuType menuType,
            int slot,
            DialogueMenuAction action
    ) {
        Objects.requireNonNull(menuType, "Menu type cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");

        if (slot < 0) {
            throw new IllegalArgumentException(
                    "Slot cannot be negative"
            );
        }

        actions.put(createKey(menuType, slot), action);
    }

    public DialogueMenuAction getAction(
            DialogueMenuHolder.MenuType menuType,
            int slot
    ) {
        Objects.requireNonNull(menuType, "Menu type cannot be null");

        return actions.getOrDefault(
                createKey(menuType, slot),
                DialogueMenuAction.NONE
        );
    }

    public boolean hasAction(
            DialogueMenuHolder.MenuType menuType,
            int slot
    ) {
        return getAction(menuType, slot) != DialogueMenuAction.NONE;
    }

    public void unregister(
            DialogueMenuHolder.MenuType menuType,
            int slot
    ) {
        Objects.requireNonNull(menuType, "Menu type cannot be null");
        actions.remove(createKey(menuType, slot));
    }

    public void clear() {
        actions.clear();
    }

    private String createKey(
            DialogueMenuHolder.MenuType menuType,
            int slot
    ) {
        return menuType.name() + ":" + slot;
    }
}
