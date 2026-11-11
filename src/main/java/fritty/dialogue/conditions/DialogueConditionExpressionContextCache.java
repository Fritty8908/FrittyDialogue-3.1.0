package fritty.dialogue.conditions;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Кеш контекстів умов для гравців.
 */
public final class DialogueConditionExpressionContextCache {

    private final ConcurrentMap<UUID, Entry> cache =
            new ConcurrentHashMap<>();

    private final long expirationMillis;
    private final int maxEntries;

    public DialogueConditionExpressionContextCache() {
        this(30_000L, 10_000);
    }

    public DialogueConditionExpressionContextCache(
            long expirationMillis,
            int maxEntries
    ) {
        if (expirationMillis < 1 || maxEntries < 1) {
            throw new IllegalArgumentException(
                    "Cache limits must be greater than zero"
            );
        }

        this.expirationMillis = expirationMillis;
        this.maxEntries = maxEntries;
    }

    public void put(
            DialogueConditionExpressionContext context
    ) {
        Objects.requireNonNull(
                context,
                "Context cannot be null"
        );

        cache.put(
                context.getPlayerId(),
                new Entry(
                        DialogueConditionExpressionContextSnapshot.capture(
                                context
                        ),
                        System.currentTimeMillis()
                )
        );

        enforceCapacity();
    }

    public DialogueConditionExpressionContext get(UUID playerId) {
        if (playerId == null) {
            return null;
        }

        Entry entry = cache.get(playerId);

        if (entry == null) {
            return null;
        }

        if (isExpired(entry)) {
            cache.remove(playerId, entry);
            return null;
        }

        return entry.snapshot().restore();
    }

    public DialogueConditionExpressionContextSnapshot getSnapshot(
            UUID playerId
    ) {
        if (playerId == null) {
            return null;
        }

        Entry entry = cache.get(playerId);

        if (entry == null) {
            return null;
        }

        if (isExpired(entry)) {
            cache.remove(playerId, entry);
            return null;
        }

        return entry.snapshot();
    }

    public boolean contains(UUID playerId) {
        return getSnapshot(playerId) != null;
    }

    public void invalidate(UUID playerId) {
        if (playerId != null) {
            cache.remove(playerId);
        }
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        cleanupExpired();
        return cache.size();
    }

    public int cleanupExpired() {
        int removed = 0;

        for (var entry : cache.entrySet()) {
            if (isExpired(entry.getValue())
                    && cache.remove(entry.getKey(), entry.getValue())) {
                removed++;
            }
        }

        return removed;
    }

    private boolean isExpired(Entry entry) {
        return System.currentTimeMillis() - entry.cachedAt()
                >= expirationMillis;
    }

    private void enforceCapacity() {
        if (cache.size() <= maxEntries) {
            return;
        }

        cleanupExpired();

        while (cache.size() > maxEntries) {
            UUID oldestId = null;
            long oldestTime = Long.MAX_VALUE;

            for (var entry : cache.entrySet()) {
                if (entry.getValue().cachedAt() < oldestTime) {
                    oldestTime = entry.getValue().cachedAt();
                    oldestId = entry.getKey();
                }
            }

            if (oldestId == null) {
                break;
            }

            cache.remove(oldestId);
        }
    }

    private record Entry(
            DialogueConditionExpressionContextSnapshot snapshot,
            long cachedAt
    ) {
    }
}
