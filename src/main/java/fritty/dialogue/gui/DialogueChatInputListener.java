package fritty.dialogue.gui;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public final class DialogueChatInputListener implements Listener {

    private final DialogueChatInputManager inputManager;

    public DialogueChatInputListener(
            DialogueChatInputManager inputManager
    ) {
        this.inputManager = Objects.requireNonNull(
                inputManager,
                "Input manager cannot be null"
        );
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (!inputManager.isWaiting(event.getPlayer())) {
            return;
        }

        inputManager.onChat(event);
    }
}
