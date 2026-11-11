package fritty.dialogue.progress;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class DialogueProgressLedger {

    private final Set<String> completedDialogues = new HashSet<>();
    private final Set<String> completedBranches = new HashSet<>();
    private final Set<String> selectedAnswers = new HashSet<>();

    public boolean completeDialogue(String dialogueId) {
        return completedDialogues.add(requireId(dialogueId));
    }

    public boolean completeBranch(String dialogueId, String branchId) {
        return completedBranches.add(
                branchKey(dialogueId, branchId)
        );
    }

    public boolean selectAnswer(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        return selectedAnswers.add(
                answerKey(dialogueId, branchId, answerId)
        );
    }

    public boolean hasCompletedDialogue(String dialogueId) {
        return completedDialogues.contains(requireId(dialogueId));
    }

    public boolean hasCompletedBranch(
            String dialogueId,
            String branchId
    ) {
        return completedBranches.contains(
                branchKey(dialogueId, branchId)
        );
    }

    public boolean hasSelectedAnswer(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        return selectedAnswers.contains(
                answerKey(dialogueId, branchId, answerId)
        );
    }

    public void removeDialogue(String dialogueId) {
        String prefix = requireId(dialogueId) + "::";

        completedDialogues.remove(dialogueId);
        completedBranches.removeIf(key -> key.startsWith(prefix));
        selectedAnswers.removeIf(key -> key.startsWith(prefix));
    }

    public void removeBranch(String dialogueId, String branchId) {
        String prefix = branchKey(dialogueId, branchId);

        completedBranches.remove(prefix);
        selectedAnswers.removeIf(
                key -> key.startsWith(prefix + "::")
        );
    }

    public void removeAnswer(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        selectedAnswers.remove(
                answerKey(dialogueId, branchId, answerId)
        );
    }

    public void clear() {
        completedDialogues.clear();
        completedBranches.clear();
        selectedAnswers.clear();
    }

    public Set<String> getCompletedDialogues() {
        return Collections.unmodifiableSet(
                new HashSet<>(completedDialogues)
        );
    }

    public Set<String> getCompletedBranches() {
        return Collections.unmodifiableSet(
                new HashSet<>(completedBranches)
        );
    }

    public Set<String> getSelectedAnswers() {
        return Collections.unmodifiableSet(
                new HashSet<>(selectedAnswers)
        );
    }

    private static String branchKey(
            String dialogueId,
            String branchId
    ) {
        return requireId(dialogueId)
                + "::"
                + requireId(branchId);
    }

    private static String answerKey(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        return branchKey(dialogueId, branchId)
                + "::"
                + requireId(answerId);
    }

    private static String requireId(String value) {
        Objects.requireNonNull(value, "ID cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }

        return value;
    }
}
