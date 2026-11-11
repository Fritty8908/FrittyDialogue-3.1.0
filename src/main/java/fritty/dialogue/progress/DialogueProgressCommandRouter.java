package fritty.dialogue.progress;

import org.bukkit.command.CommandSender;

import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class DialogueProgressCommandRouter {

    private static final Set<String> COMMANDS = Set.of(
            "reset",
            "delete",
            "add"
    );

    private final DialogueProgressCommandHandler handler;

    public DialogueProgressCommandRouter(
            DialogueProgressCommandHandler handler
    ) {
        this.handler = Objects.requireNonNull(handler);
    }

    public boolean route(CommandSender sender, String[] args) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(args);

        if (args.length == 0 || args[0] == null) {
            return false;
        }

        String command = args[0].toLowerCase(Locale.ROOT);

        if (!COMMANDS.contains(command)) {
            return false;
        }

        if (!sender.hasPermission("frittydialogue.admin")) {
            sender.sendMessage("§cУ вас немає дозволу на цю команду.");
            return true;
        }

        return handler.handle(sender, args);
    }

    public boolean isProgressCommand(String command) {
        return command != null
                && COMMANDS.contains(command.toLowerCase(Locale.ROOT));
    }
}
