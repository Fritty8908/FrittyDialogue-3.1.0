package fritty.dialogue.conditions;

import fritty.dialogue.progress.ProgressManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;

/**
 * Виконує логічні вирази умов для конкретного гравця.
 *
 * Зв'язує ExpressionService з наявною системою
 * ConditionEvaluator та ProgressManager.
 */
public final class DialogueConditionExpressionPlayerEvaluator {

    private final DialogueConditionExpressionService expressionService;

    public DialogueConditionExpressionPlayerEvaluator() {
        this(new DialogueConditionExpressionService());
    }

    public DialogueConditionExpressionPlayerEvaluator(
            DialogueConditionExpressionService expressionService
    ) {
        this.expressionService = Objects.requireNonNull(
                expressionService,
                "Expression service cannot be null"
        );
    }

    /**
     * Перевіряє вираз для гравця.
     */
    public DialogueConditionExpressionService.EvaluationResult evaluate(
            Player player,
            String expression,
            ProgressManager progressManager
    ) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(
                progressManager,
                "Progress manager cannot be null"
        );

        return expressionService.evaluate(
                expression,
                reference -> evaluateCondition(
                        player,
                        reference,
                        progressManager
                )
        );
    }

    /**
     * Перевіряє одну атомарну умову.
     *
     * Логічні оператори AND, OR та NOT
     * обробляються самим ExpressionService.
     */
    private boolean evaluateCondition(
            Player player,
            DialogueConditionExpressionParser.ConditionReference reference,
            ProgressManager progressManager
    ) {
        Condition.Type type;

        try {
            type = Condition.Type.valueOf(
                    reference.type().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Невідомий тип умови: " + reference.type(),
                    exception
            );
        }

        Condition condition = new Condition(
                type,
                reference.value()
        );

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
            throw new ProgressEvaluationException(
                    "Не вдалося перевірити прогрес гравця",
                    exception
            );
        }
    }

    /**
     * Помилка під час отримання даних прогресу.
     */
    public static final class ProgressEvaluationException
            extends RuntimeException {

        public ProgressEvaluationException(
                String message,
                Throwable cause
        ) {
            super(message, cause);
        }
    }
}
