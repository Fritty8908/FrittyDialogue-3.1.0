package fritty.dialogue.conditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Об'єднує два контексти умов, зберігаючи ідентифікатор
 * та ім'я гравця з основного контексту.
 */
public final class DialogueConditionExpressionContextMerger {

    public DialogueConditionExpressionContext merge(
            DialogueConditionExpressionContext primary,
            DialogueConditionExpressionContext secondary
    ) {
        Objects.requireNonNull(
                primary,
                "Primary context cannot be null"
        );

        if (secondary == null) {
            return primary;
        }

        if (!primary.getPlayerId().equals(secondary.getPlayerId())) {
            throw new IllegalArgumentException(
                    "Cannot merge contexts belonging to different players"
            );
        }

        Map<String, Object> merged = new LinkedHashMap<>();

        merged.putAll(primary.getVariables());
        merged.putAll(secondary.getVariables());

        return DialogueConditionExpressionContext.builder(
                        primary.getPlayerId()
                )
                .playerName(primary.getPlayerName())
                .variables(merged)
                .build();
    }

    /**
     * Об'єднує контекст гравця з додатковими змінними.
     * Значення з additionalVariables мають вищий пріоритет.
     */
    public DialogueConditionExpressionContext merge(
            DialogueConditionExpressionContext primary,
            Map<String, ?> additionalVariables
    ) {
        Objects.requireNonNull(
                primary,
                "Primary context cannot be null"
        );

        if (additionalVariables == null
                || additionalVariables.isEmpty()) {
            return primary;
        }

        Map<String, Object> merged = new LinkedHashMap<>();
        merged.putAll(primary.getVariables());

        for (Map.Entry<String, ?> entry
                : additionalVariables.entrySet()) {

            String name = entry.getKey();

            if (name == null || name.isBlank()) {
                continue;
            }

            if (entry.getValue() == null) {
                merged.remove(name);
            } else {
                merged.put(name, entry.getValue());
            }
        }

        return DialogueConditionExpressionContext.builder(
                        primary.getPlayerId()
                )
                .playerName(primary.getPlayerName())
                .variables(merged)
                .build();
    }
}
