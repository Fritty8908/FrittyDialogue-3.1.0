package fritty.dialogue.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Перевіряє не лише синтаксис виразу,
 * а й відповідність значень типам умов.
 */
public final class DialogueConditionExpressionSchemaValidator {

    private final DialogueConditionExpressionService expressionService;

    private final int maxConditions;
    private final int maxDepth;

    public DialogueConditionExpressionSchemaValidator() {
        this(
                new DialogueConditionExpressionService(),
                64,
                32
        );
    }

    public DialogueConditionExpressionSchemaValidator(
            DialogueConditionExpressionService expressionService,
            int maxConditions,
            int maxDepth
    ) {
        this.expressionService = Objects.requireNonNull(
                expressionService,
                "Expression service cannot be null"
        );

        if (maxConditions < 1 || maxDepth < 1) {
            throw new IllegalArgumentException(
                    "Limits must be greater than zero"
            );
        }

        this.maxConditions = maxConditions;
        this.maxDepth = maxDepth;
    }

    /**
     * Виконує повну перевірку виразу.
     */
    public ValidationResult validate(String expression) {
        DialogueConditionExpressionService.ValidationResult syntax =
                expressionService.validate(
                        expression,
                        maxConditions,
                        maxDepth
                );

        if (!syntax.valid()) {
            return ValidationResult.failure(
                    syntax.error(),
                    syntax.errorPosition(),
                    List.of()
            );
        }

        List<String> errors = new ArrayList<>();

        List<DialogueConditionExpressionParser.ConditionReference>
                conditions =
                expressionService.getConditions(expression);

        for (int i = 0; i < conditions.size(); i++) {
            validateCondition(
                    conditions.get(i),
                    i,
                    errors
            );
        }

        if (!errors.isEmpty()) {
            return ValidationResult.failure(
                    errors.get(0),
                    -1,
                    errors
            );
        }

        return ValidationResult.success(
                conditions.size(),
                syntax.expressionDepth()
        );
    }

    private void validateCondition(
            DialogueConditionExpressionParser.ConditionReference reference,
            int index,
            List<String> errors
    ) {
        Condition.Type type;

        try {
            type = DialogueConditionExpressionTypeRegistry.resolve(
                    reference.type()
            );
        } catch (IllegalArgumentException exception) {
            errors.add(
                    "Умова #" + (index + 1)
                            + ": невідомий тип "
                            + reference.type()
            );
            return;
        }

        String value = reference.value();

        switch (type) {
            case COMPLETED_DIALOGUE -> {
                if (value.contains(":")) {
                    errors.add(
                            "Умова #" + (index + 1)
                                    + ": DIALOGUE приймає лише ID діалогу"
                    );
                }
            }

            case COMPLETED_BRANCH -> {
                if (!hasParts(value, 2)) {
                    errors.add(
                            "Умова #" + (index + 1)
                                    + ": BRANCH повинна мати формат "
                                    + "dialogueId:branchId"
                    );
                }
            }

            case SELECTED_ANSWER -> {
                if (!hasParts(value, 3)) {
                    errors.add(
                            "Умова #" + (index + 1)
                                    + ": ANSWER повинна мати формат "
                                    + "dialogueId:branchId:answerId"
                    );
                }
            }

            case LUCKPERMS_PERMISSION -> {
                if (containsWhitespace(value)) {
                    errors.add(
                            "Умова #" + (index + 1)
                                    + ": назва permission не повинна "
                                    + "містити пробіли"
                    );
                }
            }

            case PLAYER_TAG -> {
                if (value.isBlank()) {
                    errors.add(
                            "Умова #" + (index + 1)
                                    + ": тег гравця порожній"
                    );
                }
            }
        }
    }

    private boolean hasParts(String value, int expected) {
        String[] parts = value.split(":", -1);

        if (parts.length != expected) {
            return false;
        }

        for (String part : parts) {
            if (part.isBlank()) {
                return false;
            }
        }

        return true;
    }

    private boolean containsWhitespace(String value) {
        return value.codePoints()
                .anyMatch(Character::isWhitespace);
    }

    public record ValidationResult(
            boolean valid,
            String error,
            int errorPosition,
            int conditionCount,
            int expressionDepth,
            List<String> errors
    ) {
        public ValidationResult {
            errors = errors == null
                    ? List.of()
                    : List.copyOf(errors);

            if (valid) {
                error = null;
                errorPosition = -1;
            }
        }

        private static ValidationResult success(
                int conditionCount,
                int expressionDepth
        ) {
            return new ValidationResult(
                    true,
                    null,
                    -1,
                    conditionCount,
                    expressionDepth,
                    List.of()
            );
        }

        private static ValidationResult failure(
                String error,
                int position,
                List<String> errors
        ) {
            return new ValidationResult(
                    false,
                    error,
                    position,
                    0,
                    0,
                    errors
            );
        }
    }
}
