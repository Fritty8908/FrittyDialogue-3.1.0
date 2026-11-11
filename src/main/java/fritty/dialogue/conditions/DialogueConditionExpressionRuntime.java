package fritty.dialogue.conditions;

import fritty.dialogue.progress.ProgressManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Центральний виконавець логічних виразів умов.
 *
 * Поєднує кеш аналізу, синтаксичний аналізатор
 * та перевірку умов для конкретного гравця.
 *
 * Повторні вирази використовують збережене дерево,
 * тому повторний синтаксичний аналіз не потрібен.
 */
public final class DialogueConditionExpressionRuntime {

    private final DialogueConditionExpressionCacheManager cacheManager;
    private final DialogueConditionExpressionEvaluator evaluator;

    public DialogueConditionExpressionRuntime(
            ProgressManager progressManager
    ) {
        this(
                progressManager,
                new DialogueConditionExpressionCacheManager()
        );
    }

    public DialogueConditionExpressionRuntime(
            ProgressManager progressManager,
            DialogueConditionExpressionCacheManager cacheManager
    ) {
        Objects.requireNonNull(
                progressManager,
                "Progress manager cannot be null"
        );

        this.cacheManager = Objects.requireNonNull(
                cacheManager,
                "Cache manager cannot be null"
        );

        this.evaluator =
                new DialogueConditionExpressionEvaluator(progressManager);
    }

    /**
     * Виконує логічний вираз для гравця.
     *
     * Приклад:
     * DIALOGUE:farmer AND PERMISSION:dialogue.secret
     *
     * @param player гравець
     * @param expression логічний вираз
     * @return результат перевірки
     * @throws SQLException якщо сталася помилка доступу до прогресу
     */
    public boolean evaluate(
            Player player,
            String expression
    ) throws SQLException {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        DialogueConditionExpressionService.ExpressionAnalysis analysis =
                cacheManager.analyze(expression);

        if (analysis == null || !analysis.successful()) {
            String error = analysis == null
                    ? "Не вдалося проаналізувати вираз"
                    : analysis.error();

            throw new IllegalArgumentException(
                    "Помилка виразу умов: " + error
            );
        }

        try {
            return DialogueConditionExpressionParser.evaluate(
                    analysis.root(),
                    condition -> evaluateCondition(
                            player,
                            condition
                    )
            );

        } catch (ConditionEvaluationException exception) {
            throw exception.getCause();
        }
    }

    /**
     * Виконує вираз без поширення помилок назовні.
     *
     * @param player гравець
     * @param expression логічний вираз
     * @return структурований результат
     */
    public EvaluationResult evaluateSafely(
            Player player,
            String expression
    ) {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        try {
            boolean passed = evaluate(
                    player,
                    expression
            );

            return new EvaluationResult(
                    true,
                    passed,
                    null
            );

        } catch (IllegalArgumentException exception) {
            return new EvaluationResult(
                    false,
                    false,
                    exception.getMessage()
            );

        } catch (SQLException exception) {
            return new EvaluationResult(
                    false,
                    false,
                    "Помилка доступу до прогресу гравця: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Перевіряє одну атомарну умову.
     *
     * @param player гравець
     * @param type тип умови
     * @param value значення умови
     * @return результат перевірки
     */
    public boolean evaluateCondition(
            Player player,
            String type,
            String value
    ) throws SQLException {
        return evaluator.evaluateCondition(
                player,
                type,
                value
        );
    }

    /**
     * Перевіряє одну умову з дерева виразу.
     */
    private boolean evaluateCondition(
            Player player,
            DialogueConditionExpressionParser.ConditionReference condition
    ) {
        try {
            return evaluator.evaluateCondition(
                    player,
                    condition.type(),
                    condition.value()
            );

        } catch (SQLException exception) {
            throw new ConditionEvaluationException(exception);
        }
    }

    /**
     * Перевіряє синтаксис виразу без виконання умов.
     */
    public DialogueConditionExpressionService.ExpressionAnalysis analyze(
            String expression
    ) {
        return cacheManager.analyze(expression);
    }

    /**
     * Перевіряє, чи є вираз коректним.
     */
    public boolean isValid(String expression) {
        DialogueConditionExpressionService.ExpressionAnalysis analysis =
                cacheManager.analyze(expression);

        return analysis != null && analysis.successful();
    }

    /**
     * Видаляє конкретний вираз із кешу.
     */
    public boolean invalidate(String expression) {
        return cacheManager.invalidate(expression);
    }

    /**
     * Очищає кеш виразів.
     */
    public void clearCache() {
        cacheManager.clear();
    }

    /**
     * Повертає менеджер кешу.
     */
    public DialogueConditionExpressionCacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * Повертає виконавець атомарних умов.
     */
    public DialogueConditionExpressionEvaluator getEvaluator() {
        return evaluator;
    }

    /**
     * Результат виконання виразу.
     */
    public record EvaluationResult(
            boolean successful,
            boolean passed,
            String error
    ) {
        public EvaluationResult {
            if (successful) {
                error = null;
            } else {
                passed = false;
            }
        }
    }

    /**
     * Оболонка для передачі SQLException
     * через Predicate синтаксичного аналізатора.
     */
    private static final class ConditionEvaluationException
            extends RuntimeException {

        private ConditionEvaluationException(SQLException cause) {
            super(
                    "Не вдалося перевірити умову",
                    cause
            );
        }

        @Override
        public synchronized SQLException getCause() {
            return (SQLException) super.getCause();
        }
    }
}
