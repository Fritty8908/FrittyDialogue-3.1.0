package fritty.dialogue.conditions;

import java.util.Objects;

public final class DialogueAvailabilityResult {

    private final DialogueAvailabilityMode mode;
    private final boolean available;
    private final String reason;

    private DialogueAvailabilityResult(
            DialogueAvailabilityMode mode,
            boolean available,
            String reason
    ) {
        this.mode = Objects.requireNonNull(mode);
        this.available = available;
        this.reason = reason;
    }

    public static DialogueAvailabilityResult available() {
        return new DialogueAvailabilityResult(
                DialogueAvailabilityMode.AVAILABLE,
                true,
                null
        );
    }

    public static DialogueAvailabilityResult unavailable(
            DialogueAvailabilityMode mode,
            String reason
    ) {
        if (mode == DialogueAvailabilityMode.AVAILABLE) {
            throw new IllegalArgumentException(
                    "AVAILABLE не може позначати недоступний діалог."
            );
        }

        return new DialogueAvailabilityResult(
                mode,
                false,
                reason
        );
    }

    public DialogueAvailabilityMode getMode() {
        return mode;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean shouldHide() {
        return !available && mode == DialogueAvailabilityMode.HIDE;
    }

    public boolean shouldDisable() {
        return !available && mode == DialogueAvailabilityMode.DISABLE;
    }

    public String getReason() {
        return reason;
    }
}
