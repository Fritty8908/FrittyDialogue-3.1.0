package fritty.dialogue.conditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Незмінний знімок контексту умов на конкретний момент часу.
 */
public record DialogueConditionExpressionContextSnapshot(
        UUID playerId,
        String playerName,
        Map<String, Object> variables,
        long capturedAt
) {

    public DialogueConditionExpressionContextSnapshot {
        Objects.requireNonNull(
                playerId,
                "Player ID cannot be null"
        );

        playerName = playerName == null ? "" : playerName;

        variables = variables == null
                ? Map.of()
                : Map.copyOf(new LinkedHashMap<>(variables));
    }

    public static DialogueConditionExpressionContextSnapshot capture(
            DialogueConditionExpressionContext context
    ) {
        Objects.requireNonNull(
                context,
                "Context cannot be null"
        );

        return new DialogueConditionExpressionContextSnapshot(
                context.getPlayerId(),
                context.getPlayerName(),
                context.getVariables(),
                System.currentTimeMillis()
        );
    }

    public DialogueConditionExpressionContext restore() {
        return DialogueConditionExpressionContext.builder(playerId)
                .playerName(playerName)
                .variables(variables)
                .build();
    }

    public Object getVariable(String name) {
        return name == null ? null : variables.get(name);
    }

    public boolean hasVariable(String name) {
        return name != null && variables.containsKey(name);
    }

    public int getVariableCount() {
        return variables.size();
    }

    public long ageMillis() {
        return Math.max(
                0L,
                System.currentTimeMillis() - capturedAt
        );
    }
}
