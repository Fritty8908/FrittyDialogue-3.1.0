package fritty.dialogue.storage;

import fritty.dialogue.FrittyDialogue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private final FrittyDialogue plugin;
    private Connection connection;

    public Database(FrittyDialogue plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        File folder = plugin.getDataFolder();

        if (!folder.exists() && !folder.mkdirs()) {
            throw new SQLException("Could not create plugin data folder");
        }

        File databaseFile = new File(folder, "frittydialogue.db");
        String url = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

        connection = DriverManager.getConnection(url);

        createTables();
    }

    private void createTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS dialogues (
                    id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    data TEXT NOT NULL
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_progress (
                    player_uuid TEXT NOT NULL,
                    dialogue_id TEXT NOT NULL,
                    completed INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (player_uuid, dialogue_id)
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS branch_progress (
                    player_uuid TEXT NOT NULL,
                    dialogue_id TEXT NOT NULL,
                    branch_id TEXT NOT NULL,
                    completed INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (player_uuid, dialogue_id, branch_id)
                )
                """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS answer_progress (
                    player_uuid TEXT NOT NULL,
                    dialogue_id TEXT NOT NULL,
                    branch_id TEXT NOT NULL,
                    answer_id TEXT NOT NULL,
                    selected INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (
                        player_uuid,
                        dialogue_id,
                        branch_id,
                        answer_id
                    )
                )
                """);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException exception) {
            plugin.getLogger().severe(
                    "Could not close SQLite database: "
                            + exception.getMessage()
            );
        } finally {
            connection = null;
        }
    }
}
