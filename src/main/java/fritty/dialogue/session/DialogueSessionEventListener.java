package fritty.dialogue.session;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public final class DialogueSessionEventListener implements Listener {

    private final DialogueSessionStateService stateService;

    public DialogueSessionEventListener(
            DialogueSessionStateService stateService
    ) {
        this.stateService = Objects.requireNonNull(stateService);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        stateService.terminateForDisconnect(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getServer().getScheduler().runTask(
                player.getServer().getPluginManager()
                        .getPlugin("FrittyDialogue"),
                () -> {
                    if (player.isOnline()) {
                        stateService.restorePendingLocation(player);
                    }
                }
        );
    }
}
