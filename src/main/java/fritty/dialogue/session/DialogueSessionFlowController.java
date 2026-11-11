package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionFlowController {

    private final DialogueSessionTextService textService;
    private final DialogueSessionAnswerNavigator answerNavigator;
    private final DialogueSessionAnswerSelector answerSelector;
    private final DialogueSessionBranchTransitionService transitionService;
    private final DialogueSessionAnswerExecutor answerExecutor;
    private final DialogueSessionTerminator terminator;
    private final DialogueSessionCommandExecutor commandExecutor;

    public DialogueSessionFlowController(
            DialogueSessionTextService textService,
            DialogueSessionAnswerNavigator answerNavigator,
            DialogueSessionAnswerSelector answerSelector,
            DialogueSessionBranchTransitionService transitionService,
            DialogueSessionAnswerExecutor answerExecutor,
            DialogueSessionTerminator terminator,
            DialogueSessionCommandExecutor commandExecutor
    ) {
        this.textService = Objects.requireNonNull(textService);
        this.answerNavigator = Objects.requireNonNull(answerNavigator);
        this.answerSelector = Objects.requireNonNull(answerSelector);
        this.transitionService = Objects.requireNonNull(transitionService);
        this.answerExecutor = Objects.requireNonNull(answerExecutor);
        this.terminator = Objects.requireNonNull(terminator);
        this.commandExecutor = Objects.requireNonNull(commandExecutor);
    }

    public boolean previousAnswer(DialogueSession session) {
        return answerNavigator.previous(session);
    }

    public boolean nextAnswer(DialogueSession session) {
        return answerNavigator.next(session);
    }

    public boolean confirm(Player player, DialogueSession session) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(session);

        if (!session.isActive()) {
            return false;
        }

        if (!session.isTextFinished()) {
            Branch branch = session.getCurrentBranch();

            if (branch != null) {
                commandExecutor.executeBranchCommands(player, branch);
            }

            return textService.finishText(player, session);
        }

        Branch branch = session.getCurrentBranch();

        if (branch == null) {
            terminator.end(player);
            return true;
        }

        if (hasNextBranch(branch)) {
            return transitionService.transitionToNext(session);
        }

        Answer answer = answerSelector.getSelectedAnswer(session);

        if (answer != null) {
            return answerExecutor.execute(player, session);
        }

        terminator.end(player);
        return true;
    }

    private boolean hasNextBranch(Branch branch) {
        String nextId = branch.getNextBranchId();
        return nextId != null && !nextId.isBlank();
    }
}
