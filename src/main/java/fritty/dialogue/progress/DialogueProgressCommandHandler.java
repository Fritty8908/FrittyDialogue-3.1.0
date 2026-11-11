package fritty.dialogue.progress;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;

public final class DialogueProgressCommandHandler {

    private final ProgressManager progressManager;

    public DialogueProgressCommandHandler(
            ProgressManager progressManager
    ) {
        this.progressManager = Objects.requireNonNull(progressManager);
    }

    public boolean handle(CommandSender sender, String[] args) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(args);

        if (args.length == 0) {
            return false;
        }

        String action = args[0].toLowerCase(Locale.ROOT);

        try {
            switch (action) {
                case "reset":
                    return reset(sender, args);

                case "delete":
                    return delete(sender, args);

                case "add":
                    return add(sender, args);

                default:
                    return false;
            }
        } catch (SQLException exception) {
            sender.sendMessage("§cНе вдалося змінити прогрес діалогів.");
            exception.printStackTrace();
            return true;
        }
    }

    private boolean reset(CommandSender sender, String[] args)
            throws SQLException {
        if (args.length != 2) {
            sender.sendMessage("§cВикористання: /fd reset <гравець>");
            return true;
        }

        OfflinePlayer target = findPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cГравця не знайдено.");
            return true;
        }

        progressManager.resetPlayer(target.getUniqueId());

        sender.sendMessage(
                "§aПрогрес гравця " + target.getName()
                        + " повністю скинуто."
        );
        return true;
    }

    private boolean delete(CommandSender sender, String[] args)
            throws SQLException {
        if (args.length != 3 && args.length != 4) {
            sender.sendMessage(
                    "§cВикористання: /fd delete <гравець> <діалог> [гілка]"
            );
            return true;
        }

        OfflinePlayer target = findPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cГравця не знайдено.");
            return true;
        }

        if (args.length == 3) {
            progressManager.deleteDialogueProgress(
                    target.getUniqueId(),
                    args[2]
            );

            sender.sendMessage("§aПрогрес діалогу видалено.");
            return true;
        }

        progressManager.deleteBranchProgress(
                target.getUniqueId(),
                args[2],
                args[3]
        );

        sender.sendMessage("§aПрогрес гілки видалено.");
        return true;
    }

    private boolean add(CommandSender sender, String[] args)
            throws SQLException {
        if (args.length != 3 && args.length != 4) {
            sender.sendMessage(
                    "§cВикористання: /fd add <гравець> <діалог> [гілка]"
            );
            return true;
        }

        OfflinePlayer target = findPlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cГравця не знайдено.");
            return true;
        }

        if (args.length == 3) {
            progressManager.completeDialogue(
                    target.getUniqueId(),
                    args[2]
            );

            sender.sendMessage("§aДіалог позначено завершеним.");
            return true;
        }

        progressManager.completeBranch(
                target.getUniqueId(),
                args[2],
                args[3]
        );

        sender.sendMessage("§aГілку позначено завершеною.");
        return true;
    }

    private OfflinePlayer findPlayer(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        return Bukkit.getOfflinePlayer(name);
    }
}
