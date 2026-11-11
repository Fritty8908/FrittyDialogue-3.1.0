package fritty.dialogue.conditions;

import java.util.Objects;

/**
 * Формує зрозумілі діагностичні повідомлення
 * для логічних виразів умов діалогів.
 */
public final class DialogueConditionExpressionDiagnostics {

    private final DialogueConditionExpressionService service;

    public DialogueConditionExpressionDiagnostics() {
        this(new DialogueConditionExpressionService());
    }

    public DialogueConditionExpressionDiagnostics(
            DialogueConditionExpressionService service
    ) {
        this.service = Objects.requireNonNull(service);
    }

    public Diagnostic inspect(String expression) {
        DialogueConditionExpressionService.ExpressionAnalysis analysis =
                service.analyze(expression);

        if (!analysis.successful()) {
            int position = Math.max(0, analysis.errorPosition());

            return new Diagnostic(
                    Severity.ERROR,
                    analysis.error(),
                    position,
                    lineOf(expression, position),
                    columnOf(expression, position),
                    0,
                    0
            );
        }

        DialogueConditionExpressionService.ValidationResult validation =
                service.validate(expression);

        if (!validation.valid()) {
            int position = Math.max(0, validation.errorPosition());

            return new Diagnostic(
                    Severity.ERROR,
                    validation.error(),
                    position,
                    lineOf(expression, position),
                    columnOf(expression, position),
                    validation.conditionCount(),
                    validation.expressionDepth()
            );
        }

        return new Diagnostic(
                Severity.SUCCESS,
                "Вираз умов синтаксично правильний.",
                -1,
                1,
                1,
                validation.conditionCount(),
                validation.expressionDepth()
        );
    }

    public boolean isValid(String expression) {
        return inspect(expression).severity() == Severity.SUCCESS;
    }

    private int lineOf(String expression, int position) {
        if (expression == null) {
            return 1;
        }

        int line = 1;
        int limit = Math.min(position, expression.length());

        for (int i = 0; i < limit; i++) {
            if (expression.charAt(i) == '\n') {
                line++;
            }
        }

        return line;
    }

    private int columnOf(String expression, int position) {
        if (expression == null) {
            return 1;
        }

        int limit = Math.min(position, expression.length());
        int column = 1;

        for (int i = limit - 1; i >= 0; i--) {
            if (expression.charAt(i) == '\n') {
                break;
            }

            column++;
        }

        return column;
    }

    public enum Severity {
        SUCCESS,
        WARNING,
        ERROR
    }

    public record Diagnostic(
            Severity severity,
            String message,
            int position,
            int line,
            int column,
            int conditionCount,
            int expressionDepth
    ) {
        public Diagnostic {
            Objects.requireNonNull(severity);
            Objects.requireNonNull(message);
        }

        public boolean successful() {
            return severity == Severity.SUCCESS;
        }
    }
}
