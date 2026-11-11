
package fritty.dialogue;

import fritty.dialogue.command.DialogueCommand;
import fritty.dialogue.dialogue.DialogueManager;
import fritty.dialogue.gui.DialogueMenuClickListener;
import fritty.dialogue.gui.DialogueMenuController;
import fritty.dialogue.gui.DialogueMenuManager;
import fritty.dialogue.storage.Database;
import fritty.dialogue.storage.DialogueRepository;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class FrittyDialogue extends JavaPlugin {

    private Database database;
    private DialogueManager dialogueManager;
    private DialogueMenuManager menuManager;
    private DialogueMenuController menuController;

    @Override
    public void onEnable() {
        getLogger().info("Starting FrittyDialogue...");

        try {
            initializeDatabase();
            initializeManagers();
            registerCommands();
            registerListeners();

            getLogger().info(
                    "FrittyDialogue enabled. Loaded "
                            + dialogueManager.getCount()
                            + " dialogues."
            );

        } catch (Exception exception) {
            getLogger().severe(
                    "FrittyDialogue could not start: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            if (database != null) {
                database.close();
            }

            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initializeDatabase() throws SQLException {
        database = new Database(this);
        database.connect();

        getLogger().info("SQLite database connected.");
    }

    private void initializeManagers() throws SQLException {
        DialogueRepository repository =
                new DialogueRepository(database);

        dialogueManager = new DialogueManager(repository);
        dialogueManager.loadAll();

        menuManager = new DialogueMenuManager(this);

        menuController = new DialogueMenuController(
                dialogueManager,
                menuManager
        );
    }

    private void registerCommands() {
        PluginCommand command =
                getCommand("frittydialogue");

        if (command == null) {
            throw new IllegalStateException(
                    "Command 'frittydialogue' is missing from plugin.yml"
            );
        }

        command.setExecutor(
                new DialogueCommand(
                        dialogueManager,
                        player -> menuController.openMainMenu(
                                player,
                                0
                        )
                )
        );
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(
                new DialogueMenuClickListener(
                        menuManager,
                        menuController
                ),
                this
        );
    }

    @Override
    public void onDisable() {
        if (dialogueManager != null) {
            try {
                dialogueManager.saveAll();

                getLogger().info(
                        "Dialogue data saved."
                );

            } catch (SQLException exception) {
                getLogger().severe(
                        "Could not save dialogues: "
                                + exception.getMessage()
                );

                exception.printStackTrace();
            }

            dialogueManager.unload();
        }

        if (database != null) {
            database.close();
        }

        getLogger().info("FrittyDialogue disabled.");
    }

    public Database getDatabase() {
        return database;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public DialogueMenuManager getMenuManager() {
        return menuManager;
    }

    public DialogueMenuController getMenuController() {
        return menuController;
    }
}
