package fritty.dialogue.session;

import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;

import java.util.Objects;

public final class DialogueSessionBranchNavigator {

    public boolean moveTo(
            DialogueSession session,
            String targetBranchId
    ) {
        Objects.requireNonNull(session);
        Objects.requireNonNull(targetBranchId);

        Dialogue dialogue = session.getDialogue();
        Branch target = dialogue.getBranch(targetBranchId);

        if (target == null || !session.isActive()) {
            return false;
        }

        session.setCurrentBranchId(targetBranchId);
        return true;
    }
}
