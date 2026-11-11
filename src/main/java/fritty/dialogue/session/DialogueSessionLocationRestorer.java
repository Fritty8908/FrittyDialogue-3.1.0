package fritty.dialogue.session;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionLocationRestorer {

    public boolean restore(
            Player player,
            DialogueSession session
    ) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(session, "Session cannot be null");

        Location originalLocation = session.getOriginalLocation();

        if (originalLocation == null || originalLocation.getWorld() == null) {
            return false;
        }

        return player.teleport(originalLocation.clone());
    }
}
