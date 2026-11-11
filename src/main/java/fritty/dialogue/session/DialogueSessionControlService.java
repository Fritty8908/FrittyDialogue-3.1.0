package fritty.dialogue.session;

import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionControlService {

    private final DialogueSessionTextService textService;
    private final DialogueSessionAnswerNavigator answerNavigator;
    private final DialogueSessionTerminator terminator;

    public DialogueSessionControlService(
            DialogueSessionTextService textService,
            DialogueSessionAnswerNavigator answerNavigator,
            DialogueSessionTerminator terminator
    ) {
        this.textService = Objects.requireNonNull(textService);
        this.answerNavigator = Objects.requireNonNull(answerNavigator);
        this.terminator = Objects.requireNonNull(terminator);
    }

    public boolean handlePrevious(DialogueSession session) {
        return answerNavigator.previous(session);
    }

    public boolean handleNext(DialogueSession session) {
        return answerNavigator.next(session);
    }

    public boolean handleConfirm(
            Player player,
            DialogueSession session
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(session);

        if (!session.isActive()) {
            return false;
        }

        if (!session.isTextFinished()) {
            return textService.finishText(player, session);
        }

        return terminator.end(player);
    }
}
