package fritty.dialogue.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Створює логічні вирази умов діалогів.
 *
 * Дозволяє безпечно формувати умови, групи AND / OR
 * та заперечення NOT.
 */
public final class DialogueConditionExpressionBuilder {

    private final List<String> parts = new ArrayList<>();

    public DialogueConditionExpressionBuilder add(
            String type,
            String value
    ) {
        parts.add(condition(type, value));
        return this;
    }

    public DialogueConditionExpressionBuilder addRaw(
            String expression
    ) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException(
                    "Expression cannot be empty"
            );
        }

        parts.add("(" + expression.trim() + ")");
        return this;
    }

    public DialogueConditionExpressionBuilder addAnd(
            String type,
            String value
    ) {
        return add(type, value);
    }

    public DialogueConditionExpressionBuilder addOr(
            String type,
            String value
    ) {
        if (parts.isEmpty()) {
            return add(type, value);
        }

        String previous = parts.remove(parts.size() - 1);

        parts.add(
                "(" + previous + " OR " + condition(type, value) + ")"
        );

        return this;
    }

    public DialogueConditionExpressionBuilder addNot(
            String type,
            String value
    ) {
        parts.add(
                "NOT (" + condition(type, value) + ")"
        );

        return this;
    }

    public DialogueConditionExpressionBuilder addGroup(
            String operator,
            List<String> expressions
    ) {
        Objects.requireNonNull(expressions);

        String normalizedOperator = normalizeOperator(operator);

        if (expressions.isEmpty()) {
            throw new IllegalArgumentException(
                    "Group cannot be empty"
            );
        }

        List<String> normalized = new ArrayList<>();

        for (String expression : expressions) {
            if (expression == null || expression.isBlank()) {
                throw new IllegalArgumentException(
                        "Group contains an empty expression"
                );
            }

            normalized.add("(" + expression.trim() + ")");
        }

        parts.add(
                "(" + String.join(
                        " " + normalizedOperator + " ",
                        normalized
                ) + ")"
        );

        return this;
    }

    public String build() {
        if (parts.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot build an empty expression"
            );
        }

        return String.join(" AND ", parts);
    }

    public String buildOr() {
        if (parts.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot build an empty expression"
            );
        }

        return String.join(" OR ", parts);
    }

    public DialogueConditionExpressionBuilder clear() {
        parts.clear();
        return this;
    }

    public int size() {
        return parts.size();
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    public static String condition(
            String type,
            String value
    ) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Condition type cannot be empty"
            );
        }

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Condition value cannot be empty"
            );
        }

        return type.trim().toUpperCase() + ":" + quote(value.trim());
    }

    public static String and(
            String first,
            String second
    ) {
        return combine("AND", first, second);
    }

    public static String or(
            String first,
            String second
    ) {
        return combine("OR", first, second);
    }

    public static String not(String expression) {
        validateExpression(expression);

        return "NOT (" + expression.trim() + ")";
    }

    private static String combine(
            String operator,
            String first,
            String second
    ) {
        validateExpression(first);
        validateExpression(second);

        return "(" + first.trim()
                + " " + operator + " "
                + second.trim() + ")";
    }

    private static String quote(String value) {
        if (value.matches("[a-zA-Z0-9_./%*-]+")
                && !value.equalsIgnoreCase("AND")
                && !value.equalsIgnoreCase("OR")
                && !value.equalsIgnoreCase("NOT")) {
            return value;
        }

        return "\"" + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                + "\"";
    }

    private static String normalizeOperator(String operator) {
        if (operator == null) {
            throw new IllegalArgumentException(
                    "Operator cannot be null"
            );
        }

        String normalized = operator.trim().toUpperCase();

        if (!normalized.equals("AND")
                && !normalized.equals("OR")) {
            throw new IllegalArgumentException(
                    "Operator must be AND or OR"
            );
        }

        return normalized;
    }

    private static void validateExpression(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException(
                    "Expression cannot be empty"
            );
        }
    }
}
