package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;

import java.sql.SQLException;
import java.util.Objects;

public final class DialogueSessionAnswerService {

    private final DialogueSessionProgressRecorder progressRecorder;

    public DialogueSessionAnswerService(
            DialogueSessionProgressRecorder progressRecorder
    ) {
        this.progressRecorder = Objects.requireNonNull(progressRecorder);
    }

    public void recordSelection(
            DialogueSession session,
            Answer answer
    ) throws SQLException {
        Objects.requireNonNull(session);
        Objects.requireNonNull(answer);

        progressRecorder.recordAnswerSelected(session, answer);
    }
}
