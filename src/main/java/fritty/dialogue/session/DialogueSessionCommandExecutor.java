package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public final class DialogueSessionCommandExecutor {

    public void executeBranchCommands(
            Player player,
            Branch branch
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(branch);

        executeCommands(player, branch.getCommands());
    }

    public void executeAnswerCommands(
            Player player,
            Answer answer
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(answer);

        executeCommands(player, answer.getCommands());
    }

    private void executeCommands(
            Player player,
            List<String> commands
    ) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (String command : commands) {
            if (command == null || command.isBlank()) {
                continue;
            }

            String prepared = command
                    .replace("%player%", player.getName())
                    .replace("{player}", player.getName());

            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    prepared.startsWith("/")
                            ? prepared.substring(1)
                            : prepared
            );
        }
    }
}
