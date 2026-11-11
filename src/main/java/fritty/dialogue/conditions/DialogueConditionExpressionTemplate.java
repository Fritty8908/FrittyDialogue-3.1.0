package fritty.dialogue.conditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Шаблон логічного виразу умов діалогу.
 *
 * Дозволяє зберігати вираз із параметрами
 * та підставляти їх перед перевіркою.
 */
public final class DialogueConditionExpressionTemplate {

    private final String template;
    private final Map<String, String> parameters;

    public DialogueConditionExpressionTemplate(String template) {
        if (template == null || template.isBlank()) {
            throw new IllegalArgumentException(
                    "Template cannot be empty"
            );
        }

        this.template = template;
        this.parameters = new LinkedHashMap<>();
    }

    public DialogueConditionExpressionTemplate parameter(
            String name,
            String value
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Parameter name cannot be empty"
            );
        }

        if (value == null) {
            throw new IllegalArgumentException(
                    "Parameter value cannot be null"
            );
        }

        parameters.put(name, value);
        return this;
    }

    public DialogueConditionExpressionTemplate parameters(
            Map<String, String> values
    ) {
        Objects.requireNonNull(values);

        values.forEach(this::parameter);
        return this;
    }

    public String build() {
        String result = template;

        for (Map.Entry<String, String> entry
                : parameters.entrySet()) {

            String placeholder = "${" + entry.getKey() + "}";

            result = result.replace(
                    placeholder,
                    escapeValue(entry.getValue())
            );
        }

        if (result.matches("(?s).*\\$\\{[^}]+}.*")) {
            throw new IllegalStateException(
                    "Template contains unresolved parameters"
            );
        }

        DialogueConditionExpressionParser.ParseResult parsed =
                DialogueConditionExpressionParser.parse(result);

        if (!parsed.successful()) {
            throw new IllegalArgumentException(
                    "Invalid generated expression: "
                            + parsed.firstError()
            );
        }

        return DialogueConditionExpressionParser.serialize(
                parsed.root()
        );
    }

    public boolean isValid() {
        try {
            build();
            return true;
        } catch (IllegalArgumentException
                 | IllegalStateException exception) {
            return false;
        }
    }

    public String getTemplate() {
        return template;
    }

    public Map<String, String> getParameters() {
        return Map.copyOf(parameters);
    }

    public DialogueConditionExpressionTemplate clearParameters() {
        parameters.clear();
        return this;
    }

    private String escapeValue(String value) {
        return "\""
                + value.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t")
                + "\"";
    }
}
