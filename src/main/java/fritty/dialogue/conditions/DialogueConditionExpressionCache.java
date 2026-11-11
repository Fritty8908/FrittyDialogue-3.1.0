package fritty.dialogue.conditions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Кеш результатів аналізу логічних виразів.
 *
 * Зберігає результати аналізу, щоб повторно не виконувати
 * розбір однакових виразів під час перевірки діалогів.
 *
 * Кеш має обмежений розмір і автоматично видаляє
 * найдавніші записи після досягнення ліміту.
 */
public final class DialogueConditionExpressionCache {

    private static final int DEFAULT_MAX_SIZE = 512;

    private final DialogueConditionExpressionService expressionService;
    private final int maxSize;

    private final Map<String, CacheEntry> entries;

    private long hits;
    private long misses;
    private long evictions;

    public DialogueConditionExpressionCache() {
        this(
                new DialogueConditionExpressionService(),
                DEFAULT_MAX_SIZE
        );
    }

    public DialogueConditionExpressionCache(
            int maxSize
    ) {
        this(
                new DialogueConditionExpressionService(),
                maxSize
        );
    }

    public DialogueConditionExpressionCache(
            DialogueConditionExpressionService expressionService,
            int maxSize
    ) {
        this.expressionService = Objects.requireNonNull(
                expressionService,
                "Expression service cannot be null"
        );

        if (maxSize < 1) {
            throw new IllegalArgumentException(
                    "Cache size must be greater than zero"
            );
        }

        this.maxSize = maxSize;

        this.entries = Collections.synchronizedMap(
                new LinkedHashMap<>(
                        32,
                        0.75f,
                        true
                ) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<String, CacheEntry> eldest
                    ) {
                        boolean remove = size() > DialogueConditionExpressionCache.this.maxSize;

                        if (remove) {
                            evictions++;
                        }

                        return remove;
                    }
                }
        );
    }

    /**
     * Повертає аналіз виразу з кешу або створює новий.
     */
    public DialogueConditionExpressionService.ExpressionAnalysis analyze(
            String expression
    ) {
        String key = normalizeKey(expression);

        synchronized (entries) {
            CacheEntry cached = entries.get(key);

            if (cached != null) {
                hits++;
                return cached.analysis();
            }

            misses++;
        }

        DialogueConditionExpressionService.ExpressionAnalysis analysis =
                expressionService.analyze(key);

        synchronized (entries) {
            CacheEntry existing = entries.get(key);

            if (existing != null) {
                return existing.analysis();
            }

            entries.put(
                    key,
                    new CacheEntry(analysis)
            );
        }

        return analysis;
    }

    /**
     * Перевіряє синтаксис виразу.
     *
     * Результат аналізу повторно використовується з кешу.
     */
    public boolean isValid(String expression) {
        DialogueConditionExpressionService.ExpressionAnalysis analysis =
                analyze(expression);

        return analysis != null;
    }

    /**
     * Перевіряє, чи вже є вираз у кеші.
     */
    public boolean contains(String expression) {
        String key = normalizeKey(expression);

        synchronized (entries) {
            return entries.containsKey(key);
        }
    }

    /**
     * Видаляє один вираз із кешу.
     */
    public boolean invalidate(String expression) {
        String key = normalizeKey(expression);

        synchronized (entries) {
            return entries.remove(key) != null;
        }
    }

    /**
     * Повністю очищує кеш.
     */
    public void clear() {
        synchronized (entries) {
            entries.clear();
        }
    }

    /**
     * Повертає кількість збережених виразів.
     */
    public int size() {
        synchronized (entries) {
            return entries.size();
        }
    }

    /**
     * Повертає максимальну кількість записів.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Повертає кількість успішних звернень до кешу.
     */
    public long getHits() {
        synchronized (entries) {
            return hits;
        }
    }

    /**
     * Повертає кількість звернень, для яких
     * довелося виконати новий аналіз.
     */
    public long getMisses() {
        synchronized (entries) {
            return misses;
        }
    }

    /**
     * Повертає кількість автоматично видалених записів.
     */
    public long getEvictions() {
        synchronized (entries) {
            return evictions;
        }
    }

    /**
     * Повертає частку звернень, які були оброблені кешем.
     */
    public double getHitRate() {
        synchronized (entries) {
            long total = hits + misses;

            if (total == 0) {
                return 0.0;
            }

            return (double) hits / total;
        }
    }

    /**
     * Скидає статистику використання.
     *
     * Самі записи кешу залишаються.
     */
    public void resetStatistics() {
        synchronized (entries) {
            hits = 0;
            misses = 0;
            evictions = 0;
        }
    }

    /**
     * Повертає незмінну копію ключів кешу.
     */
    public java.util.Set<String> getCachedExpressions() {
        synchronized (entries) {
            return Collections.unmodifiableSet(
                    new java.util.LinkedHashSet<>(entries.keySet())
            );
        }
    }

    /**
     * Нормалізує ключ без зміни змісту виразу.
     */
    private String normalizeKey(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException(
                    "Expression cannot be null"
            );
        }

        String normalized = expression.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "Expression cannot be empty"
            );
        }

        return normalized;
    }

    /**
     * Внутрішній запис кешу.
     */
    private record CacheEntry(
            DialogueConditionExpressionService.ExpressionAnalysis analysis
    ) {
        private CacheEntry {
            Objects.requireNonNull(
                    analysis,
                    "Analysis cannot be null"
            );
        }
    }
}
