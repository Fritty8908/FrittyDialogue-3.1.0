package fritty.dialogue.conditions;

import fritty.dialogue.progress.DialogueProgressConditionGroup;
import fritty.dialogue.progress.PlayerProgress;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueAvailabilityService {

    private final DialogueConditionService conditionService =
            new DialogueConditionService();

    public DialogueAvailabilityResult check(
            Player player,
            PlayerProgress progress,
            DialogueProgressConditionGroup conditions,
            String permission,
            String scoreboardTag,
            DialogueAvailabilityMode unavailableMode,
            String failureMessage
    ) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(unavailableMode);

        boolean available = conditionService.evaluate(
                player,
                progress,
                conditions,
                permission,
                scoreboardTag
        );

        if (available) {
            return DialogueAvailabilityResult.available();
        }

        return DialogueAvailabilityResult.unavailable(
                unavailableMode,
                failureMessage
        );
    }
}
