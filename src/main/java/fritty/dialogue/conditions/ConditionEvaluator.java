package fritty.dialogue.conditions;

import fritty.dialogue.progress.ProgressManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConditionEvaluator {

    private ConditionEvaluator() {
    }

    public static boolean evaluate(
            Player player,
            ConditionGroup group,
            ProgressManager progressManager
    ) throws SQLException {

        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(group, "Condition group cannot be null");
        Objects.requireNonNull(
                progressManager,
                "Progress manager cannot be null"
        );

        if (group.isEmpty()) {
            return true;
        }

        List<Boolean> results = new ArrayList<>();

        for (Condition condition : group.getConditions()) {
            results.add(evaluateCondition(
                    player,
                    condition,
                    progressManager
            ));
        }

        for (ConditionGroup child : group.getGroups()) {
            results.add(evaluate(
                    player,
                    child,
                    progressManager
            ));
        }

        if (group.getOperator() == ConditionGroup.Operator.AND) {
            return results.stream().allMatch(Boolean::booleanValue);
        }

        return results.stream().anyMatch(Boolean::booleanValue);
    }

    private static boolean evaluateCondition(
            Player player,
            Condition condition,
            ProgressManager progressManager
    ) throws SQLException {

        String value = condition.getValue();

        return switch (condition.getType()) {

            case COMPLETED_DIALOGUE ->
                    progressManager.hasCompletedDialogue(
                            player.getUniqueId(),
                            value
                    );

            case COMPLETED_BRANCH -> {
                String[] parts = splitValue(value, 2);

                yield progressManager.hasCompletedBranch(
                        player.getUniqueId(),
                        parts[0],
                        parts[1]
                );
            }

            case SELECTED_ANSWER -> {
                String[] parts = splitValue(value, 3);

                yield progressManager.hasSelectedAnswer(
                        player.getUniqueId(),
                        parts[0],
                        parts[1],
                        parts[2]
                );
            }

            case LUCKPERMS_PERMISSION ->
                    hasLuckPermsPermission(player, value);

            case PLAYER_TAG ->
                    player.getScoreboardTags().contains(value);
        };
    }

    private static String[] splitValue(
            String value,
            int expectedParts
    ) {
        String[] parts = value.split(":", expectedParts);

        if (parts.length != expectedParts) {
            throw new IllegalArgumentException(
                    "Invalid condition value: " + value
            );
        }

        for (String part : parts) {
            if (part.isBlank()) {
                throw new IllegalArgumentException(
                        "Condition value contains an empty part: " + value
                );
            }
        }

        return parts;
    }

    private static boolean hasLuckPermsPermission(
            Player player,
            String permission
    ) {
        if (!Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            return false;
        }

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            User user = luckPerms.getUserManager()
                    .getUser(player.getUniqueId());

            if (user == null) {
                return false;
            }

            return user.getCachedData()
                    .getPermissionData()
                    .checkPermission(permission)
                    .asBoolean();

        } catch (IllegalStateException exception) {
            return false;
        }
    }
}
