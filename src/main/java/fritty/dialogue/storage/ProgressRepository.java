package fritty.dialogue.storage;

import fritty.dialogue.progress.PlayerProgress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class ProgressRepository {

    private final Database database;

    public ProgressRepository(Database database) {
        this.database = database;
    }

    public PlayerProgress load(UUID playerId) throws SQLException {
        PlayerProgress progress = new PlayerProgress(playerId);

        Connection connection = getConnection();

        loadDialogues(connection, playerId, progress);
        loadBranches(connection, playerId, progress);
        loadAnswers(connection, playerId, progress);

        return progress;
    }

    private void loadDialogues(
            Connection connection,
            UUID playerId,
            PlayerProgress progress
    ) throws SQLException {

        String sql = """
                SELECT dialogue_id
                FROM player_progress
                WHERE player_uuid = ? AND completed = 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    progress.completeDialogue(
                            result.getString("dialogue_id")
                    );
                }
            }
        }
    }

    private void loadBranches(
            Connection connection,
            UUID playerId,
            PlayerProgress progress
    ) throws SQLException {

        String sql = """
                SELECT dialogue_id, branch_id
                FROM branch_progress
                WHERE player_uuid = ? AND completed = 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    progress.completeBranch(
                            result.getString("dialogue_id"),
                            result.getString("branch_id")
                    );
                }
            }
        }
    }

    private void loadAnswers(
            Connection connection,
            UUID playerId,
            PlayerProgress progress
    ) throws SQLException {

        String sql = """
                SELECT dialogue_id, branch_id, answer_id
                FROM answer_progress
                WHERE player_uuid = ? AND selected = 1
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    progress.selectAnswer(
                            result.getString("dialogue_id"),
                            result.getString("branch_id"),
                            result.getString("answer_id")
                    );
                }
            }
        }
    }

    public void completeDialogue(
            UUID playerId,
            String dialogueId
    ) throws SQLException {

        String sql = """
                INSERT INTO player_progress (
                    player_uuid, dialogue_id, completed
                )
                VALUES (?, ?, 1)
                ON CONFLICT(player_uuid, dialogue_id)
                DO UPDATE SET completed = 1
                """;

        execute(sql, playerId, dialogueId);
    }

    public void completeBranch(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {

        String sql = """
                INSERT INTO branch_progress (
                    player_uuid, dialogue_id, branch_id, completed
                )
                VALUES (?, ?, ?, 1)
                ON CONFLICT(player_uuid, dialogue_id, branch_id)
                DO UPDATE SET completed = 1
                """;

        execute(sql, playerId, dialogueId, branchId);
    }

    public void selectAnswer(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {

        String sql = """
                INSERT INTO answer_progress (
                    player_uuid, dialogue_id, branch_id,
                    answer_id, selected
                )
                VALUES (?, ?, ?, ?, 1)
                ON CONFLICT(
                    player_uuid, dialogue_id, branch_id, answer_id
                )
                DO UPDATE SET selected = 1
                """;

        execute(sql, playerId, dialogueId, branchId, answerId);
    }

    public void resetPlayer(UUID playerId) throws SQLException {
        Connection connection = getConnection();

        try {
            connection.setAutoCommit(false);

            String[] tables = {
                    "answer_progress",
                    "branch_progress",
                    "player_progress"
            };

            for (String table : tables) {
                String sql = "DELETE FROM " + table
                        + " WHERE player_uuid = ?";

                try (PreparedStatement statement =
                             connection.prepareStatement(sql)) {
                    statement.setString(1, playerId.toString());
                    statement.executeUpdate();
                }
            }

            connection.commit();

        } catch (SQLException exception) {
            connection.rollback();
            throw exception;

        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void execute(
            String sql,
            UUID playerId,
            String... values
    ) throws SQLException {

        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerId.toString());

            for (int i = 0; i < values.length; i++) {
                statement.setString(i + 2, values[i]);
            }

            statement.executeUpdate();
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = database.getConnection();

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database is not connected");
        }

        return connection;
    }
}
