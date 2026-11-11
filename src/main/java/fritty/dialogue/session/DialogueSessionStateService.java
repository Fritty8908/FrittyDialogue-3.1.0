package fritty.dialogue.session;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public final class DialogueSessionStateService {

    private final SessionManager sessionManager;
    private final DialoguePendingLocationRegistry pendingLocations;
    private final DialogueRenderer renderer;

    public DialogueSessionStateService(
            SessionManager sessionManager,
            DialoguePendingLocationRegistry pendingLocations,
            DialogueRenderer renderer
    ) {
        this.sessionManager = Objects.requireNonNull(sessionManager);
        this.pendingLocations = Objects.requireNonNull(pendingLocations);
        this.renderer = Objects.requireNonNull(renderer);
    }

    public DialogueSession get(UUID playerId) {
        Objects.requireNonNull(playerId);
        return sessionManager.get(playerId);
    }

    public boolean hasSession(UUID playerId) {
        Objects.requireNonNull(playerId);
        return sessionManager.hasSession(playerId);
    }

    public int getActiveCount() {
        return sessionManager.getActiveCount();
    }

    public boolean terminate(Player player) {
        Objects.requireNonNull(player);

        DialogueSession session = sessionManager.end(
                player.getUniqueId()
        );

        if (session == null) {
            return false;
        }

        restoreLocation(player, session);
        renderer.clear(player);
        return true;
    }

    public boolean terminateForDisconnect(Player player) {
        Objects.requireNonNull(player);

        DialogueSession session = sessionManager.end(
                player.getUniqueId()
        );

        if (session == null) {
            return false;
        }

        Location original = session.getOriginalLocation();

        if (original != null) {
            pendingLocations.save(player.getUniqueId(), original);
        }

        renderer.clear(player);
        return true;
    }

    public void restorePendingLocation(Player player) {
        Objects.requireNonNull(player);

        Location location = pendingLocations.take(
                player.getUniqueId()
        );

        if (location != null && location.getWorld() != null) {
            player.teleport(location);
        }
    }

    public void terminateAll() {
        for (DialogueSession session : sessionManager.getAll()) {
            if (session == null) {
                continue;
            }

            sessionManager.end(session.getPlayerId());
        }

        pendingLocations.clear();
    }

    private void restoreLocation(
            Player player,
            DialogueSession session
    ) {
        Location original = session.getOriginalLocation();

        if (original != null && original.getWorld() != null) {
            player.teleport(original);
        }
    }
}
