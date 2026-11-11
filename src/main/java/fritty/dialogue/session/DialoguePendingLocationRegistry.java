package fritty.dialogue.session;

import org.bukkit.Location;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DialoguePendingLocationRegistry {

    private final Map<UUID, Location> locations =
            new ConcurrentHashMap<>();

    public void save(UUID playerId, Location location) {
        Objects.requireNonNull(playerId);
        Objects.requireNonNull(location);

        locations.put(playerId, location.clone());
    }

    public Location take(UUID playerId) {
        Objects.requireNonNull(playerId);

        Location location = locations.remove(playerId);
        return location == null ? null : location.clone();
    }

    public boolean has(UUID playerId) {
        Objects.requireNonNull(playerId);
        return locations.containsKey(playerId);
    }

    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId);
        locations.remove(playerId);
    }

    public void clear() {
        locations.clear();
    }
}
