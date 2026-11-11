package fritty.dialogue.storage;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;

import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DialogueRepository {

    private final Database database;

    public DialogueRepository(Database database) {
        this.database = database;
    }

    public void save(Dialogue dialogue) throws SQLException {
        String sql = """
                INSERT INTO dialogues (id, name, data)
                VALUES (?, ?, ?)
                ON CONFLICT(id)
                DO UPDATE SET name = excluded.name,
                              data = excluded.data
                """;

        try (PreparedStatement statement =
                     getConnection().prepareStatement(sql)) {

            statement.setString(1, dialogue.getId());
            statement.setString(2, dialogue.getName());
            statement.setString(3, serialize(dialogue));

            statement.executeUpdate();
        }
    }

    public Dialogue load(String dialogueId) throws SQLException {
        String sql = """
                SELECT data
                FROM dialogues
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     getConnection().prepareStatement(sql)) {

            statement.setString(1, dialogueId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return deserialize(result.getString("data"));
            }
        }
    }

    public List<Dialogue> loadAll() throws SQLException {
        List<Dialogue> dialogues = new ArrayList<>();

        String sql = "SELECT data FROM dialogues ORDER BY name";

        try (PreparedStatement statement =
                     getConnection().prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                dialogues.add(deserialize(result.getString("data")));
            }
        }

        return dialogues;
    }

    public boolean delete(String dialogueId) throws SQLException {
        String sql = "DELETE FROM dialogues WHERE id = ?";

        try (PreparedStatement statement =
                     getConnection().prepareStatement(sql)) {

            statement.setString(1, dialogueId);

            return statement.executeUpdate() > 0;
        }
    }

    private String serialize(Dialogue dialogue) {
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("id", dialogue.getId());
        yaml.set("name", dialogue.getName());

        List<Map<String, Object>> branches = new ArrayList<>();

        for (Branch branch : dialogue.getAllBranches()) {
            Map<String, Object> branchData = new LinkedHashMap<>();

            branchData.put("id", branch.getId());
            branchData.put("name", branch.getName());
            branchData.put("npc-text", branch.getNpcText());
            branchData.put("root", branch.isRoot());
            branchData.put("next-branch", branch.getNextBranchId());

            List<Map<String, Object>> answers = new ArrayList<>();

            for (Answer answer : branch.getAnswers()) {
                Map<String, Object> answerData = new LinkedHashMap<>();

                answerData.put("id", answer.getId());
                answerData.put("text", answer.getText());
                answerData.put(
                        "target-branch",
                        answer.getTargetBranchId()
                );
                answerData.put("command", answer.getCommand());
                answerData.put("one-time", answer.isOneTime());

                answers.add(answerData);
            }

            branchData.put("answers", answers);
            branches.add(branchData);
        }

        yaml.set("branches", branches);

        return yaml.saveToString();
    }

    private Dialogue deserialize(String data) {
        YamlConfiguration yaml = new YamlConfiguration();

        try {
            yaml.loadFromString(data);
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Could not deserialize dialogue data",
                    exception
            );
        }

        String id = yaml.getString("id");
        String name = yaml.getString("name");

        if (id == null || name == null) {
            throw new IllegalArgumentException(
                    "Dialogue data is missing its ID or name"
            );
        }

        Dialogue dialogue = new Dialogue(id, name);

        for (Map<?, ?> branchData : yaml.getMapList("branches")) {
            String branchId = stringValue(branchData.get("id"));
            String branchName = stringValue(branchData.get("name"));

            if (branchId == null || branchName == null) {
                continue;
            }

            String npcText = stringValue(branchData.get("npc-text"));

            boolean root = Boolean.parseBoolean(
                    String.valueOf(branchData.get("root"))
            );

            Branch branch = new Branch(
                    branchId,
                    branchName,
                    npcText,
                    root
            );

            branch.setNextBranchId(
                    stringValue(branchData.get("next-branch"))
            );

            Object rawAnswers = branchData.get("answers");

            if (rawAnswers instanceof List<?> answerList) {
                for (Object rawAnswer : answerList) {
                    if (!(rawAnswer instanceof Map<?, ?> answerData)) {
                        continue;
                    }

                    String answerId = stringValue(answerData.get("id"));
                    String answerText = stringValue(answerData.get("text"));

                    if (answerId == null || answerText == null) {
                        continue;
                    }

                    Answer answer = new Answer(
                            answerId,
                            answerText,
                            stringValue(answerData.get("target-branch")),
                            stringValue(answerData.get("command")),
                            Boolean.parseBoolean(
                                    String.valueOf(
                                            answerData.get("one-time")
                                    )
                            )
                    );

                    branch.addAnswer(answer);
                }
            }

            dialogue.addBranch(branch);
        }

        return dialogue;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Connection getConnection() throws SQLException {
        Connection connection = database.getConnection();

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database is not connected");
        }

        return connection;
    }
                              }
