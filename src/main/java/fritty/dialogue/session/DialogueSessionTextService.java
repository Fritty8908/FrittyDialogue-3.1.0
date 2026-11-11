package fritty.dialogue.session;

import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionTextService {

    private final DialogueRenderer renderer;

    public DialogueSessionTextService(DialogueRenderer renderer) {
        this.renderer = Objects.requireNonNull(renderer);
    }

    public boolean finishText(
            Player player,
            DialogueSession session
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(session);

        if (session.isTextFinished()) {
            return false;
        }

        session.setTextFinished(true);
        renderer.renderTextFinished(player, session);

        return true;
    }

    public boolean isTextFinished(DialogueSession session) {
        Objects.requireNonNull(session);
        return session.isTextFinished();
    }
}
