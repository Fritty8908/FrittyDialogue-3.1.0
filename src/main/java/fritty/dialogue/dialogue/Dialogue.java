package fritty.dialogue.dialogue;

import fritty.dialogue.conditions.ConditionGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Dialogue {

    private final String id;
    private String name;

    private final Map<String, Branch> branches;
    private final DialogueSettings settings;
    private final ConditionGroup conditions;

    private String conditionFailureMessage;

    public Dialogue(String id, String name) {
        this.id = requireText(id, "Dialogue ID");
        this.name = requireText(name, "Dialogue name");

        this.branches = new LinkedHashMap<>();
        this.settings = new DialogueSettings();

        this.conditions = new ConditionGroup(
                ConditionGroup.Operator.AND
        );

        this.conditionFailureMessage =
                "Ви поки що не можете розпочати цю розмову.";
    }

    public Dialogue(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    private static String requireText(
            String value,
            String field
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " cannot be empty"
            );
        }

        return value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = requireText(name, "Dialogue name");
    }

    public Map<String, Branch> getBranches() {
        return Collections.unmodifiableMap(branches);
    }

    public Collection<Branch> getAllBranches() {
        return Collections.unmodifiableCollection(
                branches.values()
        );
    }

    public Branch getBranch(String branchId) {
        return branches.get(branchId);
    }

    public void addBranch(Branch branch) {
        Objects.requireNonNull(
                branch,
                "Branch cannot be null"
        );

        if (branches.containsKey(branch.getId())) {
            throw new IllegalArgumentException(
                    "Branch with this ID already exists"
            );
        }

        if (branch.isRoot() && hasRootBranch()) {
            throw new IllegalStateException(
                    "Dialogue already has a root branch"
            );
        }

        branches.put(branch.getId(), branch);
    }

    public boolean removeBranch(String branchId) {
        return branches.remove(branchId) != null;
    }

    public boolean hasBranch(String branchId) {
        return branches.containsKey(branchId);
    }

    public Branch getRootBranch() {
        return branches.values()
                .stream()
                .filter(Branch::isRoot)
                .findFirst()
                .orElse(null);
    }

    public boolean hasRootBranch() {
        return getRootBranch() != null;
    }

    public DialogueSettings getSettings() {
        return settings;
    }

    public ConditionGroup getConditions() {
        return conditions;
    }

    public boolean hasConditions() {
        return !conditions.isEmpty();
    }

    public String getConditionFailureMessage() {
        return conditionFailureMessage;
    }

    public void setConditionFailureMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Condition failure message cannot be empty"
            );
        }

        this.conditionFailureMessage = message;
    }
}
