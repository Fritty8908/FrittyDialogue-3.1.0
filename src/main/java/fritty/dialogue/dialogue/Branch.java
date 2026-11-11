package fritty.dialogue.dialogue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Branch {

    private final String id;
    private String name;
    private String npcText;

    private final boolean root;
    private String nextBranchId;

    private final List<Answer> answers;

    public Branch(String id, String name, String npcText, boolean root) {
        this.id = Objects.requireNonNull(id, "Branch ID cannot be null");
        this.name = Objects.requireNonNull(name, "Branch name cannot be null");
        this.npcText = npcText == null ? "" : npcText;
        this.root = root;
        this.answers = new ArrayList<>();
    }

    public Branch(String name, boolean root) {
        this(UUID.randomUUID().toString(), name, "", root);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Branch name cannot be null");
    }

    public String getNpcText() {
        return npcText;
    }

    public void setNpcText(String npcText) {
        this.npcText = npcText == null ? "" : npcText;
    }

    public boolean isRoot() {
        return root;
    }

    public String getNextBranchId() {
        return nextBranchId;
    }

    public void setNextBranchId(String nextBranchId) {
        this.nextBranchId = nextBranchId;
    }

    public boolean hasNextBranch() {
        return nextBranchId != null && !nextBranchId.isBlank();
    }

    public List<Answer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public void addAnswer(Answer answer) {
        answers.add(Objects.requireNonNull(answer, "Answer cannot be null"));
    }

    public boolean removeAnswer(String answerId) {
        return answers.removeIf(answer -> answer.getId().equals(answerId));
    }

    public Answer getAnswer(String answerId) {
        return answers.stream()
                .filter(answer -> answer.getId().equals(answerId))
                .findFirst()
                .orElse(null);
    }

    public boolean hasAnswers() {
        return !answers.isEmpty();
    }
}
