package fritty.dialogue.session;

import java.util.Objects;

public final class DialogueSessionAnswerNavigator {

    public boolean next(DialogueSession session) {
        Objects.requireNonNull(session);

        if (!session.isActive() || !session.isTextFinished()) {
            return false;
        }

        int answerCount = session.getCurrentBranch()
                .getAnswers()
                .size();

        if (answerCount == 0) {
            return false;
        }

        session.selectNextAnswer(answerCount);
        return true;
    }

    public boolean previous(DialogueSession session) {
        Objects.requireNonNull(session);

        if (!session.isActive() || !session.isTextFinished()) {
            return false;
        }

        int answerCount = session.getCurrentBranch()
                .getAnswers()
                .size();

        if (answerCount == 0) {
            return false;
        }

        session.selectPreviousAnswer(answerCount);
        return true;
    }
}
