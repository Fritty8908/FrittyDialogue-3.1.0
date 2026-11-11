package fritty.dialogue.progress;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class DialogueProgressSnapshot {

    private final UUID playerId;
    private final Set<String> completedDialogues;
    private final Set<String> completedBranches;
    private final Set<String> selectedAnswers;

    public DialogueProgressSnapshot(
            UUID playerId,
            Set<String> completedDialogues,
            Set<String> completedBranches,
            Set<String> selectedAnswers
    ) {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }

        this.playerId = playerId;
        this.completedDialogues = immutableCopy(completedDialogues);
        this.completedBranches = immutableCopy(completedBranches);
        this.selectedAnswers = immutableCopy(selectedAnswers);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Set<String> getCompletedDialogues() {
        return completedDialogues;
    }

    public Set<String> getCompletedBranches() {
        return completedBranches;
    }

    public Set<String> getSelectedAnswers() {
        return selectedAnswers;
    }

    private static Set<String> immutableCopy(Set<String> source) {
        if (source == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(
                new LinkedHashSet<>(source)
        );
    }
}
