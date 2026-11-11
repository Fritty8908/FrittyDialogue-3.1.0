package fritty.dialogue.progress;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PlayerProgress {

    private final UUID playerId;

    private final Set<String> completedDialogues = new HashSet<>();

    private final Map<String, Set<String>> completedBranches =
            new HashMap<>();

    private final Map<String, Map<String, Set<String>>> selectedAnswers =
            new HashMap<>();

    public PlayerProgress(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player UUID cannot be null");
        }

        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public boolean hasCompletedDialogue(String dialogueId) {
        return completedDialogues.contains(dialogueId);
    }

    public void completeDialogue(String dialogueId) {
        completedDialogues.add(dialogueId);
    }

    public void removeDialogue(String dialogueId) {
        completedDialogues.remove(dialogueId);
        completedBranches.remove(dialogueId);
        selectedAnswers.remove(dialogueId);
    }

    public Set<String> getCompletedDialogues() {
        return Collections.unmodifiableSet(completedDialogues);
    }

    public boolean hasCompletedBranch(
            String dialogueId,
            String branchId
    ) {
        return completedBranches
                .getOrDefault(dialogueId, Collections.emptySet())
                .contains(branchId);
    }

    public void completeBranch(String dialogueId, String branchId) {
        completedBranches
                .computeIfAbsent(dialogueId, key -> new HashSet<>())
                .add(branchId);
    }

    public void removeBranch(String dialogueId, String branchId) {
        Set<String> branches = completedBranches.get(dialogueId);

        if (branches != null) {
            branches.remove(branchId);

            if (branches.isEmpty()) {
                completedBranches.remove(dialogueId);
            }
        }

        Map<String, Set<String>> answers =
                selectedAnswers.get(dialogueId);

        if (answers != null) {
            answers.remove(branchId);

            if (answers.isEmpty()) {
                selectedAnswers.remove(dialogueId);
            }
        }
    }

    public Set<String> getCompletedBranches(String dialogueId) {
        return Collections.unmodifiableSet(
                completedBranches.getOrDefault(
                        dialogueId,
                        Collections.emptySet()
                )
        );
    }

    public boolean hasSelectedAnswer(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        return selectedAnswers
                .getOrDefault(dialogueId, Collections.emptyMap())
                .getOrDefault(branchId, Collections.emptySet())
                .contains(answerId);
    }

    public void selectAnswer(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        selectedAnswers
                .computeIfAbsent(dialogueId, key -> new HashMap<>())
                .computeIfAbsent(branchId, key -> new HashSet<>())
                .add(answerId);
    }

    public void removeAnswer(
            String dialogueId,
            String branchId,
            String answerId
    ) {
        Map<String, Set<String>> branches =
                selectedAnswers.get(dialogueId);

        if (branches == null) {
            return;
        }

        Set<String> answers = branches.get(branchId);

        if (answers == null) {
            return;
        }

        answers.remove(answerId);

        if (answers.isEmpty()) {
            branches.remove(branchId);
        }

        if (branches.isEmpty()) {
            selectedAnswers.remove(dialogueId);
        }
    }

    public void reset() {
        completedDialogues.clear();
        completedBranches.clear();
        selectedAnswers.clear();
    }
}
