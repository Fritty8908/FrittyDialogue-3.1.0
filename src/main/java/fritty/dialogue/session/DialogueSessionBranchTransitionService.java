package fritty.dialogue.session;

import fritty.dialogue.dialogue.Branch;

import java.util.Objects;

public final class DialogueSessionBranchTransitionService {

    private final DialogueSessionBranchNavigator navigator;

    public DialogueSessionBranchTransitionService(
            DialogueSessionBranchNavigator navigator
    ) {
        this.navigator = Objects.requireNonNull(navigator);
    }

    public boolean transitionToNext(DialogueSession session) {
        Objects.requireNonNull(session);

        if (!session.isActive() || !session.isTextFinished()) {
            return false;
        }

        Branch currentBranch = session.getCurrentBranch();

        if (currentBranch == null) {
            return false;
        }

        String nextBranchId = currentBranch.getNextBranchId();

        if (nextBranchId == null || nextBranchId.isBlank()) {
            return false;
        }

        return navigator.moveTo(session, nextBranchId);
    }
}
