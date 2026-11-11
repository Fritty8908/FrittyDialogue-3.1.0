package fritty.dialogue.conditions;

import java.util.Objects;

public final class Condition {

    private final Type type;
    private final String value;

    public Condition(Type type, String value) {
        this.type = Objects.requireNonNull(
                type,
                "Condition type cannot be null"
        );

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Condition value cannot be empty"
            );
        }

        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public enum Type {

        COMPLETED_DIALOGUE,
        COMPLETED_BRANCH,
        SELECTED_ANSWER,

        LUCKPERMS_PERMISSION,

        PLAYER_TAG
    }
}
