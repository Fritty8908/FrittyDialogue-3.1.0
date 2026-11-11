package fritty.dialogue.conditions;

import java.util.Objects;
import java.util.Set;

/**
 * Керує кешем логічних виразів умов діалогів.
 *
 * Надає єдину точку доступу до кешу для інших компонентів
 * системи умов.
 */
public final class DialogueConditionExpressionCacheManager {

    private final DialogueConditionExpressionCache cache;

    public DialogueConditionExpressionCacheManager() {
        this.cache = new DialogueConditionExpressionCache();
    }

    public DialogueConditionExpressionCacheManager(int maxSize) {
        this.cache = new DialogueConditionExpressionCache(maxSize);
    }

    public DialogueConditionExpressionCacheManager(
            DialogueConditionExpressionCache cache
    ) {
        this.cache = Objects.requireNonNull(
                cache,
                "Cache cannot be null"
        );
    }

    /**
     * Отримує аналіз виразу через кеш.
     */
    public DialogueConditionExpressionService.ExpressionAnalysis analyze(
            String expression
    ) {
        return cache.analyze(expression);
    }

    /**
     * Перевіряє наявність виразу в кеші.
     */
    public boolean contains(String expression) {
        return cache.contains(expression);
    }

    /**
     * Видаляє конкретний вираз із кешу.
     */
    public boolean invalidate(String expression) {
        return cache.invalidate(expression);
    }

    /**
     * Очищує всі збережені вирази.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Повертає кількість записів.
     */
    public int getSize() {
        return cache.size();
    }

    /**
     * Повертає максимальний розмір кешу.
     */
    public int getMaxSize() {
        return cache.getMaxSize();
    }

    /**
     * Повертає кількість звернень із кешу.
     */
    public long getHits() {
        return cache.getHits();
    }

    /**
     * Повертає кількість повторних аналізів.
     */
    public long getMisses() {
        return cache.getMisses();
    }

    /**
     * Повертає кількість автоматично видалених записів.
     */
    public long getEvictions() {
        return cache.getEvictions();
    }

    /**
     * Повертає коефіцієнт ефективності кешу.
     */
    public double getHitRate() {
        return cache.getHitRate();
    }

    /**
     * Скидає статистику кешу.
     */
    public void resetStatistics() {
        cache.resetStatistics();
    }

    /**
     * Повертає перелік виразів у кеші.
     */
    public Set<String> getCachedExpressions() {
        return cache.getCachedExpressions();
    }

    /**
     * Надає доступ до самого кешу для інтеграції
     * з іншими компонентами плагіна.
     */
    public DialogueConditionExpressionCache getCache() {
        return cache;
    }
}
