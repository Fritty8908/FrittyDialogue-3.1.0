package fritty.dialogue.session;

import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionStarter {

    private final DialogueSessionFactory sessionFactory;
    private final SessionManager sessionManager;
    private final DialogueRenderer renderer;

    public DialogueSessionStarter(
            DialogueSessionFactory sessionFactory,
            SessionManager sessionManager,
            DialogueRenderer renderer
    ) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public boolean start(Player player, Dialogue dialogue) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        if (sessionManager.hasSession(player.getUniqueId())) {
            player.sendMessage(
                    "§cВи вже берете участь у діалозі."
            );
            return false;
        }

        DialogueSession session = sessionFactory.create(
                player,
                dialogue
        );

        if (!sessionManager.start(session)) {
            return false;
        }

        renderer.render(player, session);
        return true;
    }
}
