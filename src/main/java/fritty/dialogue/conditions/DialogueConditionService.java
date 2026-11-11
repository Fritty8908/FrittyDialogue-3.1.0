package fritty.dialogue.conditions;

import fritty.dialogue.progress.DialogueProgressChecker;
import fritty.dialogue.progress.DialogueProgressConditionGroup;
import fritty.dialogue.progress.PlayerProgress;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueConditionService {

    private final DialogueProgressChecker progressChecker =
            new DialogueProgressChecker();

    private final DialoguePlayerConditionChecker playerChecker =
            new DialoguePlayerConditionChecker();

    public boolean evaluate(
            Player player,
            PlayerProgress progress,
            DialogueProgressConditionGroup progressConditions,
            String permission,
            String scoreboardTag
    ) {
        Objects.requireNonNull(player, "Player cannot be null");

        if (progressConditions != null
                && !progressConditions.evaluate(progress, progressChecker)) {
            return false;
        }

        return playerChecker.check(
                player,
                permission,
                scoreboardTag
        );
    }
}
