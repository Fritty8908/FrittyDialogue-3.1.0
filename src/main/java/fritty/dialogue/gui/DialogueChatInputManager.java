package fritty.dialogue.gui;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class DialogueChatInputManager {

    private final JavaPlugin plugin;

    private final Map<UUID, InputRequest> requests =
            new ConcurrentHashMap<>();

    public DialogueChatInputManager(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(
                plugin,
                "Plugin cannot be null"
        );
    }

    public void begin(
            Player player,
            String prompt,
            Consumer<String> callback
    ) {
        Objects.requireNonNull(player, "Player cannot be null");
        Objects.requireNonNull(callback, "Callback cannot be null");

        requests.put(
                player.getUniqueId(),
                new InputRequest(callback)
        );

        player.closeInventory();
        player.sendMessage(prompt);
        player.sendMessage("Напишіть значення в чат.");
        player.sendMessage("Введіть cancel для скасування.");
    }

    public boolean isWaiting(Player player) {
        return player != null
                && requests.containsKey(player.getUniqueId());
    }

    public boolean cancel(Player player) {
        if (player == null) {
            return false;
        }

        return requests.remove(player.getUniqueId()) != null;
    }

    public void cancelAll() {
        requests.clear();
    }

    public void onChat(AsyncChatEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();

        InputRequest request = requests.remove(playerId);

        if (request == null) {
            return;
        }

        event.setCancelled(true);

        String input = PlainTextComponentSerializer.plainText()
                .serialize(event.message());

        if (input.equalsIgnoreCase("cancel")) {
            plugin.getServer().getScheduler().runTask(
                    plugin,
                    () -> event.getPlayer().sendMessage(
                            "Введення скасовано."
                    )
            );
            return;
        }

        plugin.getServer().getScheduler().runTask(
                plugin,
                () -> {
                    Player player = event.getPlayer();

                    if (!player.isOnline()) {
                        return;
                    }

                    request.callback().accept(input);
                }
        );
    }

    private record InputRequest(
            Consumer<String> callback
    ) {
    }
}
