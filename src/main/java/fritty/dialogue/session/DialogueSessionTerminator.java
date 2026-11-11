package fritty.dialogue.session;

import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionTerminator {

    private final SessionManager sessionManager;
    private final DialogueSessionLocationRestorer locationRestorer;
    private final DialogueRenderer renderer;

    public DialogueSessionTerminator(
            SessionManager sessionManager,
            DialogueSessionLocationRestorer locationRestorer,
            DialogueRenderer renderer
    ) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.locationRestorer = Objects.requireNonNull(locationRestorer);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public boolean end(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        DialogueSession session = sessionManager.end(
                player.getUniqueId()
        );

        if (session == null) {
            return false;
        }

        locationRestorer.restore(player, session);
        renderer.clear(player);

        return true;
    }
}
