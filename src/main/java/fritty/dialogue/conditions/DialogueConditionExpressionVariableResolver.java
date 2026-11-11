package fritty.dialogue.conditions;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Виконує пошук змінних у контексті виразу умов.
 */
public final class DialogueConditionExpressionVariableResolver {

    private static final Pattern VARIABLE_PATTERN =
            Pattern.compile("\\$\\{([a-zA-Z0-9_.-]+)}");

    public ResolutionResult resolve(
            String expression,
            DialogueConditionExpressionContext context
    ) {
        Objects.requireNonNull(
                context,
                "Context cannot be null"
        );

        if (expression == null || expression.isBlank()) {
            return ResolutionResult.failure(
                    expression,
                    "Вираз порожній"
            );
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(expression);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);

            if (!context.hasVariable(variableName)) {
                return ResolutionResult.failure(
                        expression,
                        "Невідома змінна: " + variableName
                );
            }

            Object value = context.getVariable(variableName);

            String replacement = value == null
                    ? ""
                    : Matcher.quoteReplacement(
                            String.valueOf(value)
                    );

            matcher.appendReplacement(
                    result,
                    replacement
            );
        }

        matcher.appendTail(result);

        return ResolutionResult.success(
                result.toString()
        );
    }

    public ResolutionResult resolve(
            String expression,
            Map<String, ?> variables
    ) {
        Objects.requireNonNull(
                variables,
                "Variables cannot be null"
        );

        DialogueConditionExpressionContext context =
                DialogueConditionExpressionContext.builder(
                        new java.util.UUID(0L, 0L)
                )
                .variables(variables)
                .build();

        return resolve(expression, context);
    }

    public boolean containsVariables(String expression) {
        return expression != null
                && VARIABLE_PATTERN.matcher(expression).find();
    }

    public record ResolutionResult(
            boolean successful,
            String expression,
            String error
    ) {

        public static ResolutionResult success(String expression) {
            return new ResolutionResult(
                    true,
                    expression,
                    null
            );
        }

        public static ResolutionResult failure(
                String originalExpression,
                String error
        ) {
            return new ResolutionResult(
                    false,
                    originalExpression,
                    error
            );
        }
    }
}
