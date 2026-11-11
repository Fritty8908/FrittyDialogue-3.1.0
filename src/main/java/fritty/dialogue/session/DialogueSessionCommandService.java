package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueSessionCommandService {

    private final DialogueSessionCommandExecutor executor;

    public DialogueSessionCommandService(
            DialogueSessionCommandExecutor executor
    ) {
        this.executor = Objects.requireNonNull(executor);
    }

    public void onBranchStart(Player player, Branch branch) {
        execute(player, branch, DialogueSessionCommandTiming.ON_START);
    }

    public void afterBranchText(Player player, Branch branch) {
        execute(player, branch, DialogueSessionCommandTiming.AFTER_TEXT);
    }

    public void onAnswerSelected(Player player, Answer answer) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(answer);

        executor.executeAnswerCommands(player, answer);
    }

    public void execute(
            Player player,
            Branch branch,
            DialogueSessionCommandTiming timing
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(branch);
        Objects.requireNonNull(timing);

        if (!timing.isEnabled()) {
            return;
        }

        executor.executeBranchCommands(player, branch);
    }

    public boolean shouldExecuteOnStart(String value) {
        return DialogueSessionCommandTiming
                .fromString(value)
                .shouldRunOnStart();
    }

    public boolean shouldExecuteAfterText(String value) {
        return DialogueSessionCommandTiming
                .fromString(value)
                .shouldRunAfterText();
    }
}
