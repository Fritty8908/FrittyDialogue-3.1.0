package fritty.dialogue.conditions;

import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Створює контекст умов на основі даних гравця.
 */
public final class DialogueConditionExpressionContextFactory {

    public DialogueConditionExpressionContext create(Player player) {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        return create(player, Map.of());
    }

    public DialogueConditionExpressionContext create(
            Player player,
            Map<String, ?> additionalVariables
    ) {
        Objects.requireNonNull(
                player,
                "Player cannot be null"
        );

        Map<String, Object> variables = new LinkedHashMap<>();

        variables.put("player.name", player.getName());
        variables.put("player.uuid", player.getUniqueId().toString());
        variables.put("player.online", player.isOnline());
        variables.put("player.world", player.getWorld().getName());
        variables.put("player.x", player.getLocation().getX());
        variables.put("player.y", player.getLocation().getY());
        variables.put("player.z", player.getLocation().getZ());
        variables.put("player.health", player.getHealth());
        variables.put("player.food", player.getFoodLevel());
        variables.put("player.level", player.getLevel());
        variables.put("player.op", player.isOp());

        if (additionalVariables != null) {
            for (Map.Entry<String, ?> entry
                    : additionalVariables.entrySet()) {

                String name = entry.getKey();

                if (name == null || name.isBlank()) {
                    continue;
                }

                variables.put(name, entry.getValue());
            }
        }

        return DialogueConditionExpressionContext.builder(
                        player.getUniqueId()
                )
                .playerName(player.getName())
                .variables(variables)
                .build();
    }
}
