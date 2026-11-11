package fritty.dialogue.command;

import fritty.dialogue.dialogue.DialogueManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

public final class DialogueCommand implements CommandExecutor {

    private static final String ADMIN_PERMISSION =
            "frittydialogue.admin";

    private final DialogueManager dialogueManager;
    private final Consumer<Player> editMenuOpener;

    public DialogueCommand(
            DialogueManager dialogueManager,
            Consumer<Player> editMenuOpener
    ) {
        this.dialogueManager = Objects.requireNonNull(
                dialogueManager,
                "DialogueManager cannot be null"
        );

        this.editMenuOpener = Objects.requireNonNull(
                editMenuOpener,
                "Edit menu opener cannot be null"
        );
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "edit":
                return handleEdit(sender);

            case "reload":
                return handleReload(sender, args);

            default:
                sendHelp(sender);
                return true;
        }
    }

    private boolean handleEdit(CommandSender sender) {
        if (!hasAdminPermission(sender)) {
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    "Цю команду можна виконати лише в грі."
            );
            return true;
        }

        Player player = (Player) sender;

        try {
            editMenuOpener.accept(player);
        } catch (RuntimeException exception) {
            player.sendMessage(
                    "§cНе вдалося відкрити редактор діалогів."
            );
            exception.printStackTrace();
        }

        return true;
    }

    private boolean handleReload(
            CommandSender sender,
            String[] args
    ) {
        if (!hasAdminPermission(sender)) {
            return true;
        }

        try {
            if (args.length == 1) {
                dialogueManager.reload();

                sender.sendMessage(
                        "§aУсі діалоги успішно перезавантажено."
                );
                return true;
            }

            if (args.length == 2) {
                String dialogueId = args[1];

                boolean reloaded =
                        dialogueManager.reload(dialogueId);

                if (!reloaded) {
                    sender.sendMessage(
                            "§cДіалог не знайдено: " + dialogueId
                    );
                    return true;
                }

                sender.sendMessage(
                        "§aДіалог перезавантажено: " + dialogueId
                );
                return true;
            }

            sender.sendMessage(
                    "§cВикористання: /fd reload [dialogue-id]"
            );

        } catch (SQLException exception) {
            sender.sendMessage(
                    "§cПомилка перезавантаження діалогів."
            );

            exception.printStackTrace();
        }

        return true;
    }

    private boolean hasAdminPermission(CommandSender sender) {
        if (sender.hasPermission(ADMIN_PERMISSION)) {
            return true;
        }

        sender.sendMessage(
                "§cУ вас немає дозволу на виконання цієї команди."
        );

        return false;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§lFrittyDialogue");
        sender.sendMessage("§e/fd edit §7— відкрити редактор діалогів");
        sender.sendMessage("§e/fd reload §7— перезавантажити всі діалоги");
        sender.sendMessage(
                "§e/fd reload <id> §7— перезавантажити один діалог"
        );
    }
}
