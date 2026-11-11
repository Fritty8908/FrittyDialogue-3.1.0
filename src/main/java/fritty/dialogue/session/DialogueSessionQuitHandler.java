package fritty.dialogue.session;

import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionQuitHandler {

    private final SessionManager sessionManager;
    private final DialoguePendingLocationRegistry pendingLocations;
    private final DialogueRenderer renderer;

    public DialogueSessionQuitHandler(
            SessionManager sessionManager,
            DialoguePendingLocationRegistry pendingLocations,
            DialogueRenderer renderer
    ) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.pendingLocations = Objects.requireNonNull(pendingLocations);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public boolean handle(Player player) {
        Objects.requireNonNull(player);

        DialogueSession session = sessionManager.end(
                player.getUniqueId()
        );

        if (session == null) {
            return false;
        }

        if (session.getOriginalLocation() != null) {
            pendingLocations.save(
                    player.getUniqueId(),
                    session.getOriginalLocation()
            );
        }

        renderer.clear(player);
        return true;
    }
}
