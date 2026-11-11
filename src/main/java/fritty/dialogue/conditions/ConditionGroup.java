package fritty.dialogue.conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ConditionGroup {

    private Operator operator;

    private final List<Condition> conditions;
    private final List<ConditionGroup> groups;

    public ConditionGroup(Operator operator) {
        this.operator = Objects.requireNonNull(
                operator,
                "Condition operator cannot be null"
        );

        this.conditions = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = Objects.requireNonNull(
                operator,
                "Condition operator cannot be null"
        );
    }

    public List<Condition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public List<ConditionGroup> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public void addCondition(Condition condition) {
        conditions.add(Objects.requireNonNull(
                condition,
                "Condition cannot be null"
        ));
    }

    public boolean removeCondition(Condition condition) {
        return conditions.remove(condition);
    }

    public void addGroup(ConditionGroup group) {
        groups.add(Objects.requireNonNull(
                group,
                "Condition group cannot be null"
        ));
    }

    public boolean removeGroup(ConditionGroup group) {
        return groups.remove(group);
    }

    public boolean isEmpty() {
        return conditions.isEmpty() && groups.isEmpty();
    }

    public void clear() {
        conditions.clear();
        groups.clear();
    }

    public enum Operator {
        AND,
        OR
    }
}
