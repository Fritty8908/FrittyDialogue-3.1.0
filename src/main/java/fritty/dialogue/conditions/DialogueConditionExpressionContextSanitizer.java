package fritty.dialogue.conditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Нормалізує та очищує значення контексту умов.
 */
public final class DialogueConditionExpressionContextSanitizer {

    private final int maxNameLength;
    private final int maxStringLength;

    public DialogueConditionExpressionContextSanitizer() {
        this(128, 2048);
    }

    public DialogueConditionExpressionContextSanitizer(
            int maxNameLength,
            int maxStringLength
    ) {
        if (maxNameLength < 1 || maxStringLength < 1) {
            throw new IllegalArgumentException(
                    "Limits must be greater than zero"
            );
        }

        this.maxNameLength = maxNameLength;
        this.maxStringLength = maxStringLength;
    }

    public DialogueConditionExpressionContext sanitize(
            DialogueConditionExpressionContext context
    ) {
        Objects.requireNonNull(
                context,
                "Context cannot be null"
        );

        Map<String, Object> sanitized = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry
                : context.getVariables().entrySet()) {

            String name = sanitizeName(entry.getKey());

            if (name == null) {
                continue;
            }

            Object value = sanitizeValue(entry.getValue());

            if (value != null) {
                sanitized.put(name, value);
            }
        }

        return DialogueConditionExpressionContext.builder(
                        context.getPlayerId()
                )
                .playerName(sanitizeString(context.getPlayerName()))
                .variables(sanitized)
                .build();
    }

    private String sanitizeName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String sanitized = name.trim();

        if (sanitized.length() > maxNameLength) {
            return null;
        }

        if (!sanitized.matches("[a-zA-Z0-9_.-]+")) {
            return null;
        }

        return sanitized;
    }

    private Object sanitizeValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String stringValue) {
            return sanitizeString(stringValue);
        }

        if (value instanceof Boolean
                || value instanceof Integer
                || value instanceof Long
                || value instanceof Double
                || value instanceof Float
                || value instanceof Short
                || value instanceof Byte) {
            return value;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value instanceof java.util.UUID uuid) {
            return uuid.toString();
        }

        return null;
    }

    private String sanitizeString(String value) {
        if (value == null) {
            return "";
        }

        String sanitized = value
                .replace("\u0000", "")
                .trim();

        if (sanitized.length() > maxStringLength) {
            sanitized = sanitized.substring(0, maxStringLength);
        }

        return sanitized;
    }
}
