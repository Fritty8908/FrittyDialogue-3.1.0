package fritty.dialogue.progress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DialogueProgressConditionGroup {

    public enum Operator {
        AND,
        OR
    }

    private final Operator operator;
    private final List<DialogueProgressRequirement> requirements;
    private final List<DialogueProgressConditionGroup> groups;

    public DialogueProgressConditionGroup(Operator operator) {
        this.operator = Objects.requireNonNull(operator);
        this.requirements = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    public void addRequirement(DialogueProgressRequirement requirement) {
        requirements.add(Objects.requireNonNull(requirement));
    }

    public void addGroup(DialogueProgressConditionGroup group) {
        groups.add(Objects.requireNonNull(group));
    }

    public boolean removeRequirement(
            DialogueProgressRequirement requirement
    ) {
        return requirements.remove(requirement);
    }

    public boolean removeGroup(DialogueProgressConditionGroup group) {
        return groups.remove(group);
    }

    public Operator getOperator() {
        return operator;
    }

    public List<DialogueProgressRequirement> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    public List<DialogueProgressConditionGroup> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public boolean isEmpty() {
        return requirements.isEmpty() && groups.isEmpty();
    }

    public boolean evaluate(
            PlayerProgress progress,
            DialogueProgressChecker checker
    ) {
        Objects.requireNonNull(checker);

        if (isEmpty()) {
            return true;
        }

        for (DialogueProgressRequirement requirement : requirements) {
            boolean result = checker.isSatisfied(progress, requirement);

            if (operator == Operator.AND && !result) {
                return false;
            }

            if (operator == Operator.OR && result) {
                return true;
            }
        }

        for (DialogueProgressConditionGroup group : groups) {
            boolean result = group.evaluate(progress, checker);

            if (operator == Operator.AND && !result) {
                return false;
            }

            if (operator == Operator.OR && result) {
                return true;
            }
        }

        return operator == Operator.AND;
    }
}
