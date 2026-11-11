package fritty.dialogue.session;

import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionFactory {

    public DialogueSession create(
            Player player,
            Dialogue dialogue
    ) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        Branch rootBranch = dialogue.getRootBranch();

        if (rootBranch == null) {
            throw new IllegalStateException(
                    "Діалог не має початкової гілки: "
                            + dialogue.getId()
            );
        }

        Location originalLocation = player.getLocation().clone();

        return new DialogueSession(
                player.getUniqueId(),
                dialogue,
                rootBranch.getId(),
                originalLocation
        );
    }
}
