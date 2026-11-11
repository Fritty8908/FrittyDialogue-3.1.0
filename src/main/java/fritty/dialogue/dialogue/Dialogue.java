package fritty.dialogue.dialogue;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Dialogue {

    private final String id;
    private String name;

    private final Map<String, Branch> branches;
    private final DialogueSettings settings;

    public Dialogue(String id, String name) {
        this.id = Objects.requireNonNull(
                id,
                "Dialogue ID cannot be null"
        );

        this.name = Objects.requireNonNull(
                name,
                "Dialogue name cannot be null"
        );

        this.branches = new LinkedHashMap<>();
        this.settings = new DialogueSettings();
    }

    public Dialogue(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(
                name,
                "Dialogue name cannot be null"
        );
    }

    public Map<String, Branch> getBranches() {
        return branches;
    }

    public Collection<Branch> getAllBranches() {
        return branches.values();
    }

    public Branch getBranch(String branchId) {
        return branches.get(branchId);
    }

    public void addBranch(Branch branch) {
        Objects.requireNonNull(
                branch,
                "Branch cannot be null"
        );

        if (branch.isRoot() && getRootBranch() != null) {
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
}
