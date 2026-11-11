package fritty.dialogue.session;

import fritty.dialogue.progress.DialogueSessionProgressRecorder;

import java.sql.SQLException;
import java.util.Objects;

public final class DialogueSessionCompletionService {

    private final DialogueSessionProgressRecorder progressRecorder;

    public DialogueSessionCompletionService(
            DialogueSessionProgressRecorder progressRecorder
    ) {
        this.progressRecorder = Objects.requireNonNull(progressRecorder);
    }

    public void completeCurrentBranch(
            DialogueSession session
    ) throws SQLException {
        Objects.requireNonNull(session);
        progressRecorder.recordBranchCompleted(session);
    }

    public void completeDialogue(
            DialogueSession session
    ) throws SQLException {
        Objects.requireNonNull(session);
        progressRecorder.recordDialogueCompleted(session);
    }
}
