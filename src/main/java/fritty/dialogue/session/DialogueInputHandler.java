package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueInputHandler {

    private final DialogueRenderer renderer;
    private final SessionManager sessionManager;

    public DialogueInputHandler(
            DialogueRenderer renderer,
            SessionManager sessionManager
    ) {
        this.renderer = Objects.requireNonNull(renderer);
        this.sessionManager = Objects.requireNonNull(sessionManager);
    }

    public void handle(
            Player player,
            DialogueInputAction action
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(action);

        DialogueSession session =
                sessionManager.get(player.getUniqueId());

        if (session == null || !session.isActive()) {
            return;
        }

        Branch branch = session.getCurrentBranch();

        if (branch == null) {
            sessionManager.end(player);
            return;
        }

        if (action == DialogueInputAction.CANCEL) {
            sessionManager.end(player);
            return;
        }

        if (!session.isTextFinished()) {
            if (action == DialogueInputAction.CONFIRM) {
                session.setTextFinished(true);
                renderer.renderTextFinished(player, session);
            }

            return;
        }

        switch (action) {
            case PREVIOUS:
                session.selectPreviousAnswer(
                        branch.getAnswers().size()
                );
                renderer.render(player, session);
                break;

            case NEXT:
                session.selectNextAnswer(
                        branch.getAnswers().size()
                );
                renderer.render(player, session);
                break;

            case CONFIRM:
                confirm(player, session, branch);
                break;

            default:
                break;
        }
    }

    private void confirm(
            Player player,
            DialogueSession session,
            Branch branch
    ) {
        if (branch.hasAnswers()) {
            int index = session.getSelectedAnswerIndex();

            if (index < 0 || index >= branch.getAnswers().size()) {
                return;
            }

            Answer answer = branch.getAnswers().get(index);

            if (answer.hasTargetBranch()) {
                String targetId = answer.getTargetBranchId();

                if (!session.getDialogue().hasBranch(targetId)) {
                    player.sendMessage(
                            "§cЦільову гілку відповіді не знайдено."
                    );
                    return;
                }

                session.setCurrentBranchId(targetId);
                renderer.render(player, session);
                return;
            }

            sessionManager.end(player);
            return;
        }

        if (branch.hasNextBranch()) {
            String nextId = branch.getNextBranchId();

            if (!session.getDialogue().hasBranch(nextId)) {
                player.sendMessage(
                        "§cНаступну гілку не знайдено."
                );
                sessionManager.end(player);
                return;
            }

            session.setCurrentBranchId(nextId);
            renderer.render(player, session);
            return;
        }

        sessionManager.end(player);
    }
}
