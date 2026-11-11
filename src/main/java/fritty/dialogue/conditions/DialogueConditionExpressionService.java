package fritty.dialogue.conditions;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Сервіс роботи з логічними виразами умов діалогів.
 *
 * Відповідає за:
 * - розбір текстового виразу;
 * - перевірку синтаксису;
 * - обчислення логічного результату;
 * - отримання атомарних умов;
 * - серіалізацію дерева виразу;
 * - перевірку обмежень складності.
 *
 * Самостійно не визначає, чи виконана конкретна умова.
 * Для цього використовується переданий ConditionResolver.
 */
public final class DialogueConditionExpressionService {

    private static final int DEFAULT_MAX_CONDITIONS = 64;
    private static final int DEFAULT_MAX_DEPTH = 32;

    /**
     * Функціональний інтерфейс для перевірки атомарної умови.
     */
    @FunctionalInterface
    public interface ConditionResolver {

        boolean resolve(
                DialogueConditionExpressionParser.ConditionReference condition
        );
    }

    /**
     * Результат перевірки синтаксису.
     */
    public record ValidationResult(
            boolean valid,
            String error,
            int errorPosition,
            int conditionCount,
            int expressionDepth
    ) {
        public ValidationResult {
            if (conditionCount < 0) {
                throw new IllegalArgumentException(
                        "Condition count cannot be negative"
                );
            }

            if (expressionDepth < 0) {
                throw new IllegalArgumentException(
                        "Expression depth cannot be negative"
                );
            }

            if (valid) {
                error = null;
                errorPosition = -1;
            }
        }

        public static ValidationResult success(
                int conditionCount,
                int expressionDepth
        ) {
            return new ValidationResult(
                    true,
                    null,
                    -1,
                    conditionCount,
                    expressionDepth
            );
        }

        public static ValidationResult failure(
                String error,
                int errorPosition
        ) {
            return new ValidationResult(
                    false,
                    error == null
                            ? "Невідома помилка виразу"
                            : error,
                    Math.max(0, errorPosition),
                    0,
                    0
            );
        }
    }

    /**
     * Результат обчислення виразу.
     */
    public record EvaluationResult(
            boolean successful,
            boolean passed,
            String error,
            int errorPosition,
            List<DialogueConditionExpressionParser.ConditionReference>
                    conditions
    ) {
        public EvaluationResult {
            conditions = conditions == null
                    ? List.of()
                    : List.copyOf(conditions);

            if (successful) {
                error = null;
                errorPosition = -1;
            } else {
                passed = false;
            }
        }

        public static EvaluationResult success(
                boolean passed,
                List<DialogueConditionExpressionParser.ConditionReference>
                        conditions
        ) {
            return new EvaluationResult(
                    true,
                    passed,
                    null,
                    -1,
                    conditions
            );
        }

        public static EvaluationResult failure(
                String error,
                int errorPosition
        ) {
            return new EvaluationResult(
                    false,
                    false,
                    error == null
                            ? "Не вдалося обчислити вираз"
                            : error,
                    Math.max(0, errorPosition),
                    List.of()
            );
        }
    }

    /**
     * Результат повного аналізу виразу.
     */
    public record ExpressionAnalysis(
            boolean successful,
            DialogueConditionExpressionParser.ExpressionNode root,
            List<DialogueConditionExpressionParser.ConditionReference>
                    conditions,
            String normalizedExpression,
            String error,
            int errorPosition
    ) {
        public ExpressionAnalysis {
            conditions = conditions == null
                    ? List.of()
                    : List.copyOf(conditions);

            normalizedExpression = normalizedExpression == null
                    ? ""
                    : normalizedExpression;

            if (successful) {
                error = null;
                errorPosition = -1;
            } else {
                root = null;
            }
        }
    }

    /**
     * Перевіряє синтаксис виразу зі стандартними обмеженнями.
     */
    public ValidationResult validate(String expression) {
        return validate(
                expression,
                DEFAULT_MAX_CONDITIONS,
                DEFAULT_MAX_DEPTH
        );
    }

    /**
     * Перевіряє синтаксис і складність виразу.
     */
    public ValidationResult validate(
            String expression,
            int maxConditions,
            int maxDepth
    ) {
        if (maxConditions < 1 || maxDepth < 1) {
            return ValidationResult.failure(
                    "Обмеження виразу повинні бути більшими за нуль",
                    0
            );
        }

        DialogueConditionExpressionParser.ParseResult parsed =
                DialogueConditionExpressionParser.parse(expression);

        if (!parsed.successful()) {
            return ValidationResult.failure(
                    parsed.firstError(),
                    extractPosition(parsed)
            );
        }

        DialogueConditionExpressionParser.ExpressionNode root =
                parsed.root();

        int conditionCount =
                DialogueConditionExpressionParser.countConditions(root);

        int depth =
                DialogueConditionExpressionParser.getDepth(root);

        if (conditionCount > maxConditions) {
            return ValidationResult.failure(
                    "Перевищено максимальну кількість умов: "
                            + maxConditions,
                    0
            );
        }

        if (depth > maxDepth) {
            return ValidationResult.failure(
                    "Перевищено максимальну глибину виразу: "
                            + maxDepth,
                    0
            );
        }

        return ValidationResult.success(
                conditionCount,
                depth
        );
    }

    /**
     * Виконує повний аналіз виразу.
     */
    public ExpressionAnalysis analyze(String expression) {
        DialogueConditionExpressionParser.ParseResult parsed =
                DialogueConditionExpressionParser.parse(expression);

        if (!parsed.successful()) {
            return new ExpressionAnalysis(
                    false,
                    null,
                    List.of(),
                    "",
                    parsed.firstError(),
                    extractPosition(parsed)
            );
        }

        DialogueConditionExpressionParser.ExpressionNode root =
                parsed.root();

        List<DialogueConditionExpressionParser.ConditionReference>
                conditions =
                DialogueConditionExpressionParser.collectConditions(root);

        String normalized =
                DialogueConditionExpressionParser.serialize(root);

        return new ExpressionAnalysis(
                true,
                root,
                conditions,
                normalized,
                null,
                -1
        );
    }

    /**
     * Обчислює вираз із переданим перевіряльником умов.
     */
    public EvaluationResult evaluate(
            String expression,
            ConditionResolver resolver
    ) {
        Objects.requireNonNull(
                resolver,
                "Condition resolver cannot be null"
        );

        ExpressionAnalysis analysis = analyze(expression);

        if (!analysis.successful()) {
            return EvaluationResult.failure(
                    analysis.error(),
                    analysis.errorPosition()
            );
        }

        boolean result =
                DialogueConditionExpressionParser.evaluate(
                        analysis.root(),
                        condition -> resolver.resolve(condition)
                );

        return EvaluationResult.success(
                result,
                analysis.conditions()
        );
    }

    /**
     * Обчислює вже розібране дерево виразу.
     */
    public EvaluationResult evaluate(
            DialogueConditionExpressionParser.ExpressionNode root,
            ConditionResolver resolver
    ) {
        Objects.requireNonNull(
                root,
                "Expression root cannot be null"
        );

        Objects.requireNonNull(
                resolver,
                "Condition resolver cannot be null"
        );

        List<DialogueConditionExpressionParser.ConditionReference>
                conditions =
                DialogueConditionExpressionParser.collectConditions(root);

        boolean result =
                DialogueConditionExpressionParser.evaluate(
                        root,
                        condition -> resolver.resolve(condition)
                );

        return EvaluationResult.success(
                result,
                conditions
        );
    }

    /**
     * Повертає всі атомарні умови виразу.
     */
    public List<DialogueConditionExpressionParser.ConditionReference>
    getConditions(String expression) {
        ExpressionAnalysis analysis = analyze(expression);

        if (!analysis.successful()) {
            return List.of();
        }

        return analysis.conditions();
    }

    /**
     * Перевіряє наявність конкретного типу умови.
     */
    public boolean containsConditionType(
            String expression,
            String conditionType
    ) {
        if (conditionType == null || conditionType.isBlank()) {
            return false;
        }

        ExpressionAnalysis analysis = analyze(expression);

        if (!analysis.successful()) {
            return false;
        }

        return DialogueConditionExpressionParser.containsType(
                analysis.root(),
                conditionType
        );
    }

    /**
     * Повертає кількість атомарних умов.
     */
    public int countConditions(String expression) {
        ExpressionAnalysis analysis = analyze(expression);

        return analysis.successful()
                ? analysis.conditions().size()
                : 0;
    }

    /**
     * Повертає глибину дерева виразу.
     */
    public int getDepth(String expression) {
        ExpressionAnalysis analysis = analyze(expression);

        if (!analysis.successful()) {
            return 0;
        }

        return DialogueConditionExpressionParser.getDepth(
                analysis.root()
        );
    }

    /**
     * Перетворює вираз у нормалізований текст.
     */
    public String normalizeExpression(String expression) {
        ExpressionAnalysis analysis = analyze(expression);

        return analysis.successful()
                ? analysis.normalizedExpression()
                : null;
    }

    /**
     * Перевіряє, чи можна безпечно використовувати вираз
     * з установленими обмеженнями.
     */
    public boolean isSafe(
            String expression,
            int maxConditions,
            int maxDepth
    ) {
        return validate(
                expression,
                maxConditions,
                maxDepth
        ).valid();
    }

    /**
     * Витягує позицію помилки з результату парсера.
     */
    private int extractPosition(
            DialogueConditionExpressionParser.ParseResult parsed
    ) {
        if (parsed == null || parsed.errors().isEmpty()) {
            return 0;
        }

        return Math.max(
                0,
                parsed.errors().get(0).position()
        );
    }
}
