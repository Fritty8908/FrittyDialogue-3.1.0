package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;

import java.util.List;
import java.util.Objects;

public final class DialogueSessionAnswerSelector {

    public Answer getSelectedAnswer(DialogueSession session) {
        Objects.requireNonNull(session);

        if (!session.isActive() || !session.isTextFinished()) {
            return null;
        }

        Branch branch = session.getCurrentBranch();
        if (branch == null) {
            return null;
        }

        List<Answer> answers = branch.getAnswers();
        if (answers.isEmpty()) {
            return null;
        }

        int index = session.getSelectedAnswerIndex();

        if (index < 0 || index >= answers.size()) {
            return null;
        }

        return answers.get(index);
    }
}
