package fritty.dialogue.session;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public final class DialogueRenderer {

    private static final String PREFIX = "§8[§6Діалог§8] §f";

    public void render(Player player, DialogueSession session) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(session, "Session cannot be null");

        if (!session.isActive()) {
            return;
        }

        Branch branch = session.getCurrentBranch();

        if (branch == null) {
            player.sendMessage(PREFIX + "§cПоточну гілку не знайдено.");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§6§l" + branch.getName());
        player.sendMessage("§f" + branch.getNpcText());
        player.sendMessage("");

        if (!session.isTextFinished()) {
            player.sendMessage("§7Натисніть §eF§7, щоб показати весь текст.");
            return;
        }

        renderAnswers(player, session, branch);
    }

    public void renderAnswers(
            Player player,
            DialogueSession session,
            Branch branch
    ) {
        List<Answer> answers = branch.getAnswers();

        if (answers.isEmpty()) {
            if (branch.hasNextBranch()) {
                player.sendMessage(
                        "§7Натисніть §eF§7, щоб продовжити."
                );
            } else {
                player.sendMessage(
                        "§7Натисніть §eF§7, щоб завершити діалог."
                );
            }

            return;
        }

        player.sendMessage("§8Ваші відповіді:");

        int selected = session.getSelectedAnswerIndex();

        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);

            String marker = i == selected
                    ? "§e▶ "
                    : "§7  ";

            player.sendMessage(
                    marker + "§f" + answer.getText()
            );
        }

        player.sendMessage("");
        player.sendMessage(
                "§7Керування: §eW§7 — назад, §eS§7 — далі, "
                        + "§eF§7 — вибрати"
        );
    }

    public void renderTextFinished(
            Player player,
            DialogueSession session
    ) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(session, "Session cannot be null");

        if (!session.isActive()) {
            return;
        }

        Branch branch = session.getCurrentBranch();

        if (branch != null) {
            renderAnswers(player, session, branch);
        }
    }

    public void clear(Player player) {
        Objects.requireNonNull(player, "Player cannot be null");

        player.sendMessage("");
    }
}
