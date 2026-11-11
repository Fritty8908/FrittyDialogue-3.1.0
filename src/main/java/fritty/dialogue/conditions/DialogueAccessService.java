package fritty.dialogue.conditions;

import fritty.dialogue.progress.DialogueProgressConditionGroup;
import fritty.dialogue.progress.PlayerProgress;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialogueAccessService {

    private final DialogueAvailabilityService availabilityService =
            new DialogueAvailabilityService();

    private final DialogueOneTimeChecker oneTimeChecker =
            new DialogueOneTimeChecker();

    public DialogueAvailabilityResult checkDialogue(
            Player player,
            PlayerProgress progress,
            String dialogueId,
            boolean oneTime,
            DialogueProgressConditionGroup conditions,
            String permission,
            String scoreboardTag,
            DialogueAvailabilityMode mode,
            String failureMessage
    ) {
        Objects.requireNonNull(player);

        if (!oneTimeChecker.canStartDialogue(
                progress,
                dialogueId,
                oneTime
        )) {
            return unavailable(mode, "Цей діалог уже завершено.");
        }

        return availabilityService.check(
                player,
                progress,
                conditions,
                permission,
                scoreboardTag,
                mode,
                failureMessage
        );
    }

    public DialogueAvailabilityResult checkBranch(
            Player player,
            PlayerProgress progress,
            String dialogueId,
            String branchId,
            boolean oneTime,
            DialogueProgressConditionGroup conditions,
            String permission,
            String scoreboardTag,
            DialogueAvailabilityMode mode,
            String failureMessage
    ) {
        Objects.requireNonNull(player);

        if (!oneTimeChecker.canStartBranch(
                progress,
                dialogueId,
                branchId,
                oneTime
        )) {
            return unavailable(mode, "Цю гілку вже завершено.");
        }

        return availabilityService.check(
                player,
                progress,
                conditions,
                permission,
                scoreboardTag,
                mode,
                failureMessage
        );
    }

    public DialogueAvailabilityResult checkAnswer(
            Player player,
            PlayerProgress progress,
            String dialogueId,
            String branchId,
            String answerId,
            boolean oneTime,
            DialogueProgressConditionGroup conditions,
            String permission,
            String scoreboardTag,
            DialogueAvailabilityMode mode,
            String failureMessage
    ) {
        Objects.requireNonNull(player);

        if (!oneTimeChecker.canSelectAnswer(
                progress,
                dialogueId,
                branchId,
                answerId,
                oneTime
        )) {
            return unavailable(mode, "Цю відповідь уже використано.");
        }

        return availabilityService.check(
                player,
                progress,
                conditions,
                permission,
                scoreboardTag,
                mode,
                failureMessage
        );
    }

    private DialogueAvailabilityResult unavailable(
            DialogueAvailabilityMode mode,
            String reason
    ) {
        return DialogueAvailabilityResult.unavailable(
                Objects.requireNonNull(mode),
                reason
        );
    }
}
