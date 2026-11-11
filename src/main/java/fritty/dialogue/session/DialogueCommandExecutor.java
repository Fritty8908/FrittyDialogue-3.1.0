package fritty.dialogue.session;

import fritty.dialogue.dialogue.Dialogue;
import fritty.dialogue.dialogue.DialogueManager;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueCommandExecutor {

    public enum Timing {
        NONE,
        ON_START,
        AFTER_TEXT
    }

    private final DialogueManager dialogueManager;

    public DialogueCommandExecutor(DialogueManager dialogueManager) {
        this.dialogueManager = Objects.requireNonNull(dialogueManager);
    }

    public void execute(
            Player player,
            Dialogue dialogue,
            String command,
            Timing timing,
            Timing event
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(dialogue);
        Objects.requireNonNull(timing);
        Objects.requireNonNull(event);

        if (command == null || command.isBlank()
                || timing == Timing.NONE
                || timing != event) {
            return;
        }

        String prepared = command.replace(
                "{player}",
                player.getName()
        );

        player.getServer().dispatchCommand(
                player.getServer().getConsoleSender(),
                prepared
        );
    }

    public Dialogue getDialogue(String dialogueId) {
        if (dialogueId == null || dialogueId.isBlank()) {
            return null;
        }

        return dialogueManager.get(dialogueId);
    }
}
