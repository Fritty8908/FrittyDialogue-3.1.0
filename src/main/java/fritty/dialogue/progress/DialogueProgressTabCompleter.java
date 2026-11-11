package fritty.dialogue.progress;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class DialogueProgressTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length == 1) {
            return filter(
                    List.of("edit", "reload", "reset", "delete", "add"),
                    args[0]
            );
        }

        if (args.length == 2 && isProgressCommand(args[0])) {
            List<String> players = new ArrayList<>();

            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                String name = player.getName();

                if (name != null && !name.isBlank()) {
                    players.add(name);
                }
            }

            return filter(players, args[1]);
        }

        if (args.length == 3
                && (args[0].equalsIgnoreCase("delete")
                || args[0].equalsIgnoreCase("add"))) {
            return Collections.emptyList();
        }

        if (args.length == 4
                && (args[0].equalsIgnoreCase("delete")
                || args[0].equalsIgnoreCase("add"))) {
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private boolean isProgressCommand(String value) {
        return value.equalsIgnoreCase("reset")
                || value.equalsIgnoreCase("delete")
                || value.equalsIgnoreCase("add");
    }

    private List<String> filter(List<String> values, String input) {
        String prefix = input.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                result.add(value);
            }
        }

        return result;
    }
}
