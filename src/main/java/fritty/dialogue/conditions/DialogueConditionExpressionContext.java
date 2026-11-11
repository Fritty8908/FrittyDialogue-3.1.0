package fritty.dialogue.conditions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Контекст виконання виразу умов діалогу.
 *
 * Зберігає дані гравця та додаткові значення,
 * доступні під час перевірки умов.
 */
public final class DialogueConditionExpressionContext {

    private final UUID playerId;
    private final String playerName;
    private final Map<String, Object> variables;

    private DialogueConditionExpressionContext(
            UUID playerId,
            String playerName,
            Map<String, Object> variables
    ) {
        this.playerId = Objects.requireNonNull(
                playerId,
                "Player ID cannot be null"
        );

        this.playerName = playerName == null
                ? ""
                : playerName;

        this.variables = Collections.unmodifiableMap(
                new LinkedHashMap<>(
                        variables == null
                                ? Map.of()
                                : variables
                )
        );
    }

    public static Builder builder(UUID playerId) {
        return new Builder(playerId);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public Object getVariable(String name) {
        if (name == null) {
            return null;
        }

        return variables.get(name);
    }

    public boolean hasVariable(String name) {
        return name != null && variables.containsKey(name);
    }

    public String getString(String name) {
        Object value = getVariable(name);

        return value == null
                ? null
                : String.valueOf(value);
    }

    public boolean getBoolean(String name) {
        Object value = getVariable(name);

        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }

        if (value instanceof String stringValue) {
            return Boolean.parseBoolean(stringValue);
        }

        return false;
    }

    public int getInt(String name, int defaultValue) {
        Object value = getVariable(name);

        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    public double getDouble(
            String name,
            double defaultValue
    ) {
        Object value = getVariable(name);

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value instanceof String stringValue) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }

        return defaultValue;
    }

    public static final class Builder {

        private final UUID playerId;

        private String playerName = "";

        private final Map<String, Object> variables =
                new LinkedHashMap<>();

        private Builder(UUID playerId) {
            this.playerId = Objects.requireNonNull(
                    playerId,
                    "Player ID cannot be null"
            );
        }

        public Builder playerName(String playerName) {
            this.playerName = playerName == null
                    ? ""
                    : playerName;

            return this;
        }

        public Builder variable(
                String name,
                Object value
        ) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(
                        "Variable name cannot be empty"
                );
            }

            if (value == null) {
                variables.remove(name);
            } else {
                variables.put(name, value);
            }

            return this;
        }

        public Builder variables(
                Map<String, ?> values
        ) {
            if (values == null) {
                return this;
            }

            for (Map.Entry<String, ?> entry : values.entrySet()) {
                variable(
                        entry.getKey(),
                        entry.getValue()
                );
            }

            return this;
        }

        public Builder removeVariable(String name) {
            if (name != null) {
                variables.remove(name);
            }

            return this;
        }

        public Builder clearVariables() {
            variables.clear();
            return this;
        }

        public DialogueConditionExpressionContext build() {
            return new DialogueConditionExpressionContext(
                    playerId,
                    playerName,
                    variables
            );
        }
    }
}
