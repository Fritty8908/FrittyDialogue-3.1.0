package fritty.dialogue.session;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SessionManager {

    private final Map<UUID, DialogueSession> sessions =
            new HashMap<>();

    public synchronized boolean start(DialogueSession session) {
        if (session == null) {
            throw new IllegalArgumentException(
                    "Session cannot be null"
            );
        }

        UUID playerId = session.getPlayerId();

        if (sessions.containsKey(playerId)) {
            return false;
        }

        sessions.put(playerId, session);
        return true;
    }

    public synchronized DialogueSession get(UUID playerId) {
        if (playerId == null) {
            return null;
        }

        return sessions.get(playerId);
    }

    public synchronized boolean hasSession(UUID playerId) {
        return playerId != null
                && sessions.containsKey(playerId);
    }

    public synchronized DialogueSession end(UUID playerId) {
        if (playerId == null) {
            return null;
        }

        DialogueSession session = sessions.remove(playerId);

        if (session != null) {
            session.end();
        }

        return session;
    }

    public synchronized boolean remove(UUID playerId) {
        return end(playerId) != null;
    }

    public synchronized int getActiveCount() {
        return sessions.size();
    }

    public synchronized Collection<DialogueSession> getAll() {
        return Collections.unmodifiableList(
                sessions.values().stream().toList()
        );
    }

    public synchronized void endAll() {
        for (DialogueSession session : sessions.values()) {
            session.end();
        }

        sessions.clear();
    }
}
