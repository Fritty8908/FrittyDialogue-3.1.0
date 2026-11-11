package fritty.dialogue.session;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class DialoguePendingLocationListener implements Listener {

    private final JavaPlugin plugin;
    private final DialoguePendingLocationRegistry registry;

    public DialoguePendingLocationListener(
            JavaPlugin plugin,
            DialoguePendingLocationRegistry registry
    ) {
        this.plugin = Objects.requireNonNull(plugin);
        this.registry = Objects.requireNonNull(registry);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Location pendingLocation = registry.take(
                player.getUniqueId()
        );

        if (pendingLocation == null) {
            return;
        }

        plugin.getServer().getScheduler().runTask(
                plugin,
                () -> {
                    if (player.isOnline()
                            && pendingLocation.getWorld() != null) {
                        player.teleport(pendingLocation);
                    }
                }
        );
    }
}
