package fritty.dialogue.dialogue;

import fritty.dialogue.conditions.ConditionGroup;

import java.util.Objects;
import java.util.UUID;

public final class Answer {

    private final String id;
    private String text;

    private String targetBranchId;
    private String command;

    private boolean oneTime;

    private final ConditionGroup conditions;

    private AvailabilityMode unavailableMode;
    private AvailabilityMode usedMode;

    public Answer(
            String id,
            String text,
            String targetBranchId,
            String command,
            boolean oneTime
    ) {
        this.id = Objects.requireNonNull(
                id,
                "Answer ID cannot be null"
        );

        this.text = Objects.requireNonNull(
                text,
                "Answer text cannot be null"
        );

        this.targetBranchId = targetBranchId;
        this.command = command;
        this.oneTime = oneTime;

        this.conditions = new ConditionGroup(
                ConditionGroup.Operator.AND
        );

        this.unavailableMode = AvailabilityMode.HIDE;
        this.usedMode = AvailabilityMode.DISABLE;
    }

    public Answer(String text) {
        this(
                UUID.randomUUID().toString(),
                text,
                null,
                null,
                false
        );
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = Objects.requireNonNull(
                text,
                "Answer text cannot be null"
        );
    }

    public String getTargetBranchId() {
        return targetBranchId;
    }

    public void setTargetBranchId(String targetBranchId) {
        this.targetBranchId = targetBranchId;
    }

    public boolean hasTargetBranch() {
        return targetBranchId != null
                && !targetBranchId.isBlank();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command == null || command.isBlank()
                ? null
                : command;
    }

    public boolean hasCommand() {
        return command != null && !command.isBlank();
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public ConditionGroup getConditions() {
        return conditions;
    }

    public boolean hasConditions() {
        return !conditions.isEmpty();
    }

    public AvailabilityMode getUnavailableMode() {
        return unavailableMode;
    }

    public void setUnavailableMode(AvailabilityMode mode) {
        this.unavailableMode = Objects.requireNonNull(
                mode,
                "Unavailable mode cannot be null"
        );
    }

    public AvailabilityMode getUsedMode() {
        return usedMode;
    }

    public void setUsedMode(AvailabilityMode mode) {
        this.usedMode = Objects.requireNonNull(
                mode,
                "Used mode cannot be null"
        );
    }

    public enum AvailabilityMode {

        HIDE,
        DISABLE,
        AVAILABLE
    }
}
