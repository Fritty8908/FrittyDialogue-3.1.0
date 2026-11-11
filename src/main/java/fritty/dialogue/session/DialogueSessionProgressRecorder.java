package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.progress.ProgressManager;

import java.sql.SQLException;
import java.util.Objects;

public final class DialogueSessionProgressRecorder {

    private final ProgressManager progressManager;

    public DialogueSessionProgressRecorder(
            ProgressManager progressManager
    ) {
        this.progressManager = Objects.requireNonNull(progressManager);
    }

    public void recordBranchCompleted(
            DialogueSession session
    ) throws SQLException {
        Objects.requireNonNull(session);

        progressManager.completeBranch(
                session.getPlayerId(),
                session.getDialogueId(),
                session.getCurrentBranchId()
        );
    }

    public void recordAnswerSelected(
            DialogueSession session,
            Answer answer
    ) throws SQLException {
        Objects.requireNonNull(session);
        Objects.requireNonNull(answer);

        progressManager.selectAnswer(
                session.getPlayerId(),
                session.getDialogueId(),
                session.getCurrentBranchId(),
                answer.getId()
        );
    }

    public void recordDialogueCompleted(
            DialogueSession session
    ) throws SQLException {
        Objects.requireNonNull(session);

        progressManager.completeDialogue(
                session.getPlayerId(),
                session.getDialogueId()
        );
    }
}
