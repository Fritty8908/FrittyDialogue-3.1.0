package fritty.dialogue.conditions;

import fritty.dialogue.progress.ProgressManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;

/**
 * Виконує логічні вирази умов безпосередньо для гравця.
 *
 * Поєднує:
 * - DialogueConditionExpressionParser;
 * - DialogueConditionExpressionService;
 * - Condition;
 * - ConditionGroup;
 * - ConditionEvaluator;
 * - ProgressManager.
 *
 * Підтримувані типи:
 * DIALOGUE
 * DIALOGUE_COMPLETED
 * COMPLETED_DIALOGUE
 * BRANCH
 * BRANCH_COMPLETED
 * COMPLETED_BRANCH
 * ANSWER
 * ANSWER_SELECTED
 * SELECTED_ANSWER
 * PERMISSION
 * HAS_PERMISSION
 * LUCKPERMS_PERMISSION
 * TAG
 * HAS_TAG
 * SCOREBOARD_TAG
 * PLAYER_TAG
 */
public final class DialogueConditionExpressionEvaluator {

    private final ProgressManager progressManager;
    private final DialogueConditionExpressionService expressionService;

    public DialogueConditionExpressionEvaluator(
            ProgressManager progressManager
    ) {
        this.progressManager = Objects.requireNonNull(
                progressManager,
                "Progress manager cannot be null"
        );

        this.expressionService =
                new DialogueConditionExpressionService();
    }

    /**
     * Перевіряє логічний вираз для гравця.
     *
     * Приклад:
     * DIALOGUE:farmer AND PERMISSION:dialogue.secret
     */
    public boolean evaluate(
            Player player,
            String expression
    ) throws SQLException {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        DialogueConditionExpressionService.EvaluationResult result;

        try {
            result = expressionService.evaluate(
                    expression,
                    condition -> resolve(player, condition)
            );
        } catch (ConditionEvaluationException exception) {
            throw exception.getCause();
        }

        if (!result.successful()) {
            throw new IllegalArgumentException(
                    "Помилка виразу умов: " + result.error()
            );
        }

        return result.passed();
    }

    /**
     * Перевіряє вираз і повертає результат без винятку
     * для синтаксично неправильного виразу.
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
            boolean passed = evaluate(player, expression);

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
     */
    public boolean evaluateCondition(
            Player player,
            String type,
            String value
    ) throws SQLException {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        DialogueConditionExpressionParser.ConditionReference reference =
                new DialogueConditionExpressionParser.ConditionReference(
                        type,
                        value
                );

        try {
            return resolve(player, reference);

        } catch (ConditionEvaluationException exception) {
            throw exception.getCause();
        }
    }

    /**
     * Перетворює посилання парсера на звичайну умову
     * та передає її на перевірку ConditionEvaluator.
     */
    private boolean resolve(
            Player player,
            DialogueConditionExpressionParser.ConditionReference reference
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(reference);

        Condition condition = convert(reference);

        ConditionGroup group = new ConditionGroup(
                ConditionGroup.Operator.AND
        );

        group.addCondition(condition);

        try {
            return ConditionEvaluator.evaluate(
                    player,
                    group,
                    progressManager
            );

        } catch (SQLException exception) {
            throw new ConditionEvaluationException(exception);
        }
    }

    /**
     * Перетворює тип і значення виразу
     * на типізовану умову системи.
     */
    private Condition convert(
            DialogueConditionExpressionParser.ConditionReference reference
    ) {
        String type = normalize(reference.type());
        String value = normalizeValue(reference.value());

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "Значення умови не може бути порожнім"
            );
        }

        return switch (type) {

            case "DIALOGUE",
                 "DIALOGUE_COMPLETED",
                 "COMPLETED_DIALOGUE" ->
                    new Condition(
                            Condition.Type.COMPLETED_DIALOGUE,
                            value
                    );

            case "BRANCH",
                 "BRANCH_COMPLETED",
                 "COMPLETED_BRANCH" ->
                    new Condition(
                            Condition.Type.COMPLETED_BRANCH,
                            validateCompositeValue(value, 2, type)
                    );

            case "ANSWER",
                 "ANSWER_SELECTED",
                 "SELECTED_ANSWER" ->
                    new Condition(
                            Condition.Type.SELECTED_ANSWER,
                            validateCompositeValue(value, 3, type)
                    );

            case "PERMISSION",
                 "HAS_PERMISSION",
                 "LUCKPERMS_PERMISSION" ->
                    new Condition(
                            Condition.Type.LUCKPERMS_PERMISSION,
                            value
                    );

            case "TAG",
                 "HAS_TAG",
                 "SCOREBOARD_TAG",
                 "PLAYER_TAG" ->
                    new Condition(
                            Condition.Type.PLAYER_TAG,
                            value
                    );

            default -> throw new IllegalArgumentException(
                    "Невідомий тип умови: " + type
            );
        };
    }

    /**
     * Перевіряє складене значення.
     *
     * BRANCH:
     * dialogueId:branchId
     *
     * ANSWER:
     * dialogueId:branchId:answerId
     */
    private String validateCompositeValue(
            String value,
            int expectedParts,
            String type
    ) {
        String[] parts = value.split(":", -1);

        if (parts.length != expectedParts) {
            throw new IllegalArgumentException(
                    "Неправильний формат умови " + type
                            + ". Очікується частин: "
                            + expectedParts
            );
        }

        for (String part : parts) {
            if (part.isBlank()) {
                throw new IllegalArgumentException(
                        "Умова " + type
                                + " містить порожній ідентифікатор"
                );
            }
        }

        return String.join(":", parts);
    }

    /**
     * Нормалізує назву типу умови.
     */
    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Нормалізує значення без зміни його регістру.
     */
    private String normalizeValue(String value) {
        return value == null
                ? ""
                : value.trim();
    }

    /**
     * Повертає сервіс аналізу логічних виразів.
     */
    public DialogueConditionExpressionService getExpressionService() {
        return expressionService;
    }

    /**
     * Повертає менеджер прогресу.
     */
    public ProgressManager getProgressManager() {
        return progressManager;
    }

    /**
     * Результат перевірки виразу.
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
     * Внутрішня оболонка для передачі SQLException
     * через функціональний інтерфейс Predicate.
     */
    private static final class ConditionEvaluationException
            extends RuntimeException {

        private ConditionEvaluationException(SQLException cause) {
            super(
                    "Не вдалося перевірити умову прогресу",
                    cause
            );
        }

        @Override
        public synchronized SQLException getCause() {
            return (SQLException) super.getCause();
        }
    }
}
