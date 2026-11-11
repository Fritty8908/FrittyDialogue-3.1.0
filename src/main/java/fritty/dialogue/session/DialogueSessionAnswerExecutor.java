package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionAnswerExecutor {

    private final DialogueSessionAnswerSelector selector;
    private final DialogueSessionBranchNavigator navigator;
    private final DialogueSessionAnswerService answerService;

    public DialogueSessionAnswerExecutor(
            DialogueSessionAnswerSelector selector,
            DialogueSessionBranchNavigator navigator,
            DialogueSessionAnswerService answerService
    ) {
        this.selector = Objects.requireNonNull(selector);
        this.navigator = Objects.requireNonNull(navigator);
        this.answerService = Objects.requireNonNull(answerService);
    }

    public boolean execute(Player player, DialogueSession session) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(session);

        Answer answer = selector.getSelectedAnswer(session);

        if (answer == null) {
            return false;
        }

        try {
            answerService.recordSelection(session, answer);
        } catch (Exception exception) {
            player.sendMessage("§cНе вдалося зберегти вибір відповіді.");
            return false;
        }

        String targetBranchId = answer.getTargetBranchId();

        if (targetBranchId == null || targetBranchId.isBlank()) {
            return false;
        }

        return navigator.moveTo(session, targetBranchId);
    }
}
