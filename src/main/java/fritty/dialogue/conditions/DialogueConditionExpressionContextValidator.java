package fritty.dialogue.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Перевіряє коректність контексту перед виконанням
 * виразу умов діалогу.
 */
public final class DialogueConditionExpressionContextValidator {

    private final int maxVariables;
    private final int maxVariableNameLength;
    private final int maxStringValueLength;

    public DialogueConditionExpressionContextValidator() {
        this(128, 128, 2048);
    }

    public DialogueConditionExpressionContextValidator(
            int maxVariables,
            int maxVariableNameLength,
            int maxStringValueLength
    ) {
        if (maxVariables < 1
                || maxVariableNameLength < 1
                || maxStringValueLength < 1) {
            throw new IllegalArgumentException(
                    "Validation limits must be greater than zero"
            );
        }

        this.maxVariables = maxVariables;
        this.maxVariableNameLength = maxVariableNameLength;
        this.maxStringValueLength = maxStringValueLength;
    }

    public ValidationResult validate(
            DialogueConditionExpressionContext context
    ) {
        List<String> errors = new ArrayList<>();

        if (context == null) {
            errors.add("Контекст не може бути null");

            return ValidationResult.failure(errors);
        }

        UUID playerId = context.getPlayerId();

        if (playerId == null) {
            errors.add("UUID гравця відсутній");
        }

        String playerName = context.getPlayerName();

        if (playerName != null
                && playerName.length() > maxStringValueLength) {
            errors.add("Ім'я гравця перевищує допустиму довжину");
        }

        Map<String, Object> variables = context.getVariables();

        if (variables.size() > maxVariables) {
            errors.add(
                    "Кількість змінних перевищує ліміт "
                            + maxVariables
            );
        }

        for (Map.Entry<String, Object> entry
                : variables.entrySet()) {

            validateVariable(
                    entry.getKey(),
                    entry.getValue(),
                    errors
            );
        }

        if (!errors.isEmpty()) {
            return ValidationResult.failure(errors);
        }

        return ValidationResult.success(variables.size());
    }

    private void validateVariable(
            String name,
            Object value,
            List<String> errors
    ) {
        if (name == null || name.isBlank()) {
            errors.add("Назва змінної порожня");
            return;
        }

        if (name.length() > maxVariableNameLength) {
            errors.add(
                    "Назва змінної перевищує допустиму довжину: "
                            + name
            );
        }

        if (!name.matches("[a-zA-Z0-9_.-]+")) {
            errors.add(
                    "Недопустимі символи в назві змінної: "
                            + name
            );
        }

        if (value == null) {
            errors.add(
                    "Значення змінної не може бути null: "
                            + name
            );
            return;
        }

        if (value instanceof String stringValue) {
            if (stringValue.length() > maxStringValueLength) {
                errors.add(
                        "Значення змінної перевищує допустиму довжину: "
                                + name
                );
            }

            return;
        }

        if (value instanceof Number
                || value instanceof Boolean
                || value instanceof UUID) {
            return;
        }

        errors.add(
                "Непідтримуваний тип значення змінної: "
                        + name
                        + " ("
                        + value.getClass().getSimpleName()
                        + ")"
        );
    }

    public record ValidationResult(
            boolean valid,
            int variableCount,
            List<String> errors
    ) {

        public ValidationResult {
            errors = errors == null
                    ? List.of()
                    : List.copyOf(errors);
        }

        public static ValidationResult success(int variableCount) {
            return new ValidationResult(
                    true,
                    variableCount,
                    List.of()
            );
        }

        public static ValidationResult failure(List<String> errors) {
            return new ValidationResult(
                    false,
                    0,
                    errors
            );
        }

        public String firstError() {
            return errors.isEmpty()
                    ? null
                    : errors.get(0);
        }
    }
}
