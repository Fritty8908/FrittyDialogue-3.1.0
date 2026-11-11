package fritty.dialogue.session;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public final class DialogueSessionListener implements Listener {

    private final SessionManager sessionManager;

    public DialogueSessionListener(SessionManager sessionManager) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        sessionManager.end(event.getPlayer());
    }
}
