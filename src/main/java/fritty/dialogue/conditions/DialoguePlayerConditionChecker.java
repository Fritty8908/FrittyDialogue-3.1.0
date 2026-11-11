package fritty.dialogue.conditions;

import org.bukkit.entity.Player;

import java.util.Objects;

public final class DialoguePlayerConditionChecker {

    public boolean hasPermission(Player player, String permission) {
        Objects.requireNonNull(player);
        requireText(permission, "permission");

        return player.hasPermission(permission);
    }

    public boolean hasScoreboardTag(Player player, String tag) {
        Objects.requireNonNull(player);
        requireText(tag, "tag");

        return player.getScoreboardTags().contains(tag);
    }

    public boolean check(
            Player player,
            String permission,
            String scoreboardTag
    ) {
        Objects.requireNonNull(player);

        if (permission != null && !permission.isBlank()
                && !hasPermission(player, permission)) {
            return false;
        }

        if (scoreboardTag != null && !scoreboardTag.isBlank()
                && !hasScoreboardTag(player, scoreboardTag)) {
            return false;
        }

        return true;
    }

    private void requireText(String value, String field) {
        Objects.requireNonNull(value, field + " cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " cannot be blank"
            );
        }
    }
}
