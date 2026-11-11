package fritty.dialogue.conditions;

import java.util.Objects;
import java.util.UUID;

/**
 * Керує життєвим циклом контекстів умов гравців.
 */
public final class DialogueConditionExpressionContextLifecycle {

    private final DialogueConditionExpressionContextCache cache;

    public DialogueConditionExpressionContextLifecycle(
            DialogueConditionExpressionContextCache cache
    ) {
        this.cache = Objects.requireNonNull(
                cache,
                "Context cache cannot be null"
        );
    }

    /**
     * Видаляє контекст гравця після виходу із сервера.
     */
    public void onPlayerQuit(UUID playerId) {
        invalidate(playerId);
    }

    /**
     * Видаляє контекст після завершення діалогу.
     */
    public void onDialogueEnd(UUID playerId) {
        invalidate(playerId);
    }

    /**
     * Видаляє контекст після зміни стану гравця.
     */
    public void onPlayerStateChanged(UUID playerId) {
        invalidate(playerId);
    }

    public void invalidate(UUID playerId) {
        if (playerId != null) {
            cache.invalidate(playerId);
        }
    }

    /**
     * Очищує всі контексти.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Видаляє прострочені записи.
     */
    public int cleanup() {
        return cache.cleanupExpired();
    }

    public int getCachedCount() {
        return cache.size();
    }
}
