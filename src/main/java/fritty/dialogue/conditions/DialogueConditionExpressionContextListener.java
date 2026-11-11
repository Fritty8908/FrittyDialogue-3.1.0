package fritty.dialogue.conditions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

/**
 * Відстежує події гравців для актуалізації кешу контекстів.
 */
public final class DialogueConditionExpressionContextListener
        implements Listener {

    private final DialogueConditionExpressionContextLifecycle lifecycle;

    public DialogueConditionExpressionContextListener(
            DialogueConditionExpressionContextLifecycle lifecycle
    ) {
        this.lifecycle = Objects.requireNonNull(
                lifecycle,
                "Context lifecycle cannot be null"
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lifecycle.onPlayerQuit(
                event.getPlayer().getUniqueId()
        );
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        lifecycle.onPlayerStateChanged(
                event.getPlayer().getUniqueId()
        );
    }
        }
