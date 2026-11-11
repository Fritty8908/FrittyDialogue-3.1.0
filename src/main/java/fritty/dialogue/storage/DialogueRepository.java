package fritty.dialogue.storage;

import fritty.dialogue.dialogue.Answer;
import fritty.dialogue.dialogue.Branch;
import fritty.dialogue.dialogue.Dialogue;
import fritty.dialogue.dialogue.DialogueSettings;
import fritty.dialogue.dialogue.SettingKey;
import fritty.dialogue.conditions.Condition;
import fritty.dialogue.conditions.ConditionGroup;
import org.bukkit.configuration.ConfigurationSection;
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
                INSERT OR REPLACE INTO dialogues (id, name, data)
                VALUES (?, ?, ?)
                """;

        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("dialogue", serializeDialogue(dialogue));

        try (PreparedStatement statement =
                     getConnection().prepareStatement(sql)) {

            statement.setString(1, dialogue.getId());
            statement.setString(2, dialogue.getName());
            statement.setString(3, yaml.saveToString());

            statement.executeUpdate();
        }
    }

    public Dialogue load(String dialogueId) throws SQLException {
        String sql = """
                SELECT data FROM dialogues WHERE id = ?
                """;

        try (PreparedStatement statement =
                     getConnection().prepareStatement(sql)) {

            statement.setString(1, dialogueId);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return deserializeDialogue(
                        result.getString("data")
                );
            }
        }
    }

    public List<Dialogue> loadAll() throws SQLException {
        String sql = "SELECT data FROM dialogues ORDER BY rowid";
        List<Dialogue> dialogues = new ArrayList<>();

        try (PreparedStatement statement =
                     getConnection().prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                dialogues.add(
                        deserializeDialogue(
                                result.getString("data")
                        )
                );
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

    private Map<String, Object> serializeDialogue(
            Dialogue dialogue
    ) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("id", dialogue.getId());
        data.put("name", dialogue.getName());

        data.put(
                "settings",
                serializeSettings(dialogue.getSettings())
        );

        data.put(
                "conditions",
                serializeConditionGroup(dialogue.getConditions())
        );

        data.put(
                "condition-failure-message",
                dialogue.getConditionFailureMessage()
        );

        List<Map<String, Object>> branches = new ArrayList<>();

        for (Branch branch : dialogue.getAllBranches()) {
            branches.add(serializeBranch(branch));
        }

        data.put("branches", branches);

        return data;
    }

    private Map<String, Object> serializeBranch(Branch branch) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("id", branch.getId());
        data.put("name", branch.getName());
        data.put("npc-text", branch.getNpcText());
        data.put("root", branch.isRoot());
        data.put("next-branch", branch.getNextBranchId());

        data.put(
                "settings",
                serializeSettings(branch.getSettings())
        );

        data.put(
                "conditions",
                serializeConditionGroup(branch.getConditions())
        );

        data.put("command", branch.getCommand());
        data.put(
                "command-timing",
                branch.getCommandTiming().name()
        );

        List<Map<String, Object>> answers = new ArrayList<>();

        for (Answer answer : branch.getAnswers()) {
            answers.add(serializeAnswer(answer));
        }

        data.put("answers", answers);

        return data;
    }

    private Map<String, Object> serializeAnswer(Answer answer) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("id", answer.getId());
        data.put("text", answer.getText());
        data.put("target-branch", answer.getTargetBranchId());
        data.put("command", answer.getCommand());
        data.put("one-time", answer.isOneTime());

        data.put(
                "conditions",
                serializeConditionGroup(answer.getConditions())
        );

        data.put(
                "unavailable-mode",
                answer.getUnavailableMode().name()
        );

        data.put(
                "used-mode",
                answer.getUsedMode().name()
        );

        return data;
    }

    private Map<String, Object> serializeSettings(
            DialogueSettings settings
    ) {
        return new LinkedHashMap<>(settings.getAll());
    }

    private Map<String, Object> serializeConditionGroup(
            ConditionGroup group
    ) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("operator", group.getOperator().name());

        List<Map<String, Object>> conditions = new ArrayList<>();

        for (Condition condition : group.getConditions()) {
            Map<String, Object> item = new LinkedHashMap<>();

            item.put("type", condition.getType().name());
            item.put("value", condition.getValue());

            conditions.add(item);
        }

        data.put("conditions", conditions);

        List<Map<String, Object>> groups = new ArrayList<>();

        for (ConditionGroup child : group.getGroups()) {
            groups.add(serializeConditionGroup(child));
        }

        data.put("groups", groups);

        return data;
    }

    private Dialogue deserializeDialogue(String source) {
        YamlConfiguration yaml = new YamlConfiguration();

        try {
            yaml.loadFromString(source);
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Could not read dialogue data",
                    exception
            );
        }

        ConfigurationSection section =
                yaml.getConfigurationSection("dialogue");

        if (section == null) {
            throw new IllegalArgumentException(
                    "Dialogue data is missing"
            );
        }

        String id = section.getString("id");
        String name = section.getString("name");

        Dialogue dialogue = new Dialogue(id, name);

        loadSettings(
                dialogue.getSettings(),
                section.getConfigurationSection("settings")
        );

        loadConditionGroup(
                dialogue.getConditions(),
                section.getConfigurationSection("conditions")
        );

        String failureMessage = section.getString(
                "condition-failure-message"
        );

        if (failureMessage != null && !failureMessage.isBlank()) {
            dialogue.setConditionFailureMessage(failureMessage);
        }

        List<?> branches = section.getList("branches", List.of());

        for (Object item : branches) {
            if (item instanceof Map<?, ?> map) {
                Branch branch = deserializeBranch(map);
                dialogue.addBranch(branch);
            }
        }

        return dialogue;
    }

    private Branch deserializeBranch(Map<?, ?> data) {
        String id = stringValue(data.get("id"), null);
        String name = stringValue(data.get("name"), "Branch");
        String npcText = stringValue(data.get("npc-text"), "");
        boolean root = booleanValue(data.get("root"), false);

        Branch branch = new Branch(id, name, npcText, root);

        branch.setNextBranchId(
                stringValue(data.get("next-branch"), null)
        );

        loadSettings(
                branch.getSettings(),
                asMap(data.get("settings"))
        );

        loadConditionGroup(
                branch.getConditions(),
                asMap(data.get("conditions"))
        );

        branch.setCommand(
                stringValue(data.get("command"), null)
        );

        branch.setCommandTiming(
                enumValue(
                        Branch.CommandTiming.class,
                        data.get("command-timing"),
                        Branch.CommandTiming.NONE
                )
        );

        Object answerObject = data.get("answers");

        if (answerObject instanceof List<?> answers) {
            for (Object item : answers) {
                if (item instanceof Map<?, ?> answerData) {
                    branch.addAnswer(
                            deserializeAnswer(answerData)
                    );
                }
            }
        }

        return branch;
    }

    private Answer deserializeAnswer(Map<?, ?> data) {
        String id = stringValue(data.get("id"), null);
        String text = stringValue(data.get("text"), "");

        Answer answer = new Answer(
                id,
                text,
                stringValue(data.get("target-branch"), null),
                stringValue(data.get("command"), null),
                booleanValue(data.get("one-time"), false)
        );

        loadConditionGroup(
                answer.getConditions(),
                asMap(data.get("conditions"))
        );

        answer.setUnavailableMode(
                enumValue(
                        Answer.AvailabilityMode.class,
                        data.get("unavailable-mode"),
                        Answer.AvailabilityMode.HIDE
                )
        );

        answer.setUsedMode(
                enumValue(
                        Answer.AvailabilityMode.class,
                        data.get("used-mode"),
                        Answer.AvailabilityMode.DISABLE
                )
        );

        return answer;
    }

    private void loadSettings(
            DialogueSettings settings,
            Map<?, ?> values
    ) {
        if (values == null) {
            return;
        }

        for (Map.Entry<?, ?> entry : values.entrySet()) {
            if (!(entry.getKey() instanceof String key)) {
                continue;
            }

            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            SettingKey settingKey = SettingKey.fromKey(key);

            if (settingKey == null) {
                continue;
            }

            Object converted = convertSettingValue(
                    settingKey,
                    value
            );

            if (converted != null) {
                settings.set(key, converted);
            }
        }
    }

    private Object convertSettingValue(
            SettingKey key,
            Object value
    ) {
        return switch (key.getType()) {
            case STRING -> value instanceof String ? value : null;

            case BOOLEAN -> value instanceof Boolean ? value : null;

            case INTEGER -> {
                if (value instanceof Number number) {
                    yield number.intValue();
                }
                yield null;
            }

            case DOUBLE -> {
                if (value instanceof Number number) {
                    yield number.doubleValue();
                }
                yield null;
            }
        };
    }

    private void loadConditionGroup(
            ConditionGroup target,
            Map<?, ?> data
    ) {
        if (data == null) {
            return;
        }

        target.setOperator(
                enumValue(
                        ConditionGroup.Operator.class,
                        data.get("operator"),
                        ConditionGroup.Operator.AND
                )
        );

        Object conditionsObject = data.get("conditions");

        if (conditionsObject instanceof List<?> conditions) {
            for (Object item : conditions) {
                if (!(item instanceof Map<?, ?> conditionData)) {
                    continue;
                }

                Condition.Type type = enumValue(
                        Condition.Type.class,
                        conditionData.get("type"),
                        null
                );

                String value = stringValue(
                        conditionData.get("value"),
                        null
                );

                if (type != null && value != null && !value.isBlank()) {
                    target.addCondition(
                            new Condition(type, value)
                    );
                }
            }
        }

        Object groupsObject = data.get("groups");

        if (groupsObject instanceof List<?> groups) {
            for (Object item : groups) {
                if (!(item instanceof Map<?, ?> childData)) {
                    continue;
                }

                ConditionGroup child = new ConditionGroup(
                        enumValue(
                                ConditionGroup.Operator.class,
                                childData.get("operator"),
                                ConditionGroup.Operator.AND
                        )
                );

                loadConditionGroup(child, childData);
                target.addGroup(child);
            }
        }
    }

    private Map<?, ?> asMap(Object value) {
        return value instanceof Map<?, ?> map ? map : null;
    }

    private String stringValue(Object value, String fallback) {
        return value instanceof String string ? string : fallback;
    }

    private boolean booleanValue(Object value, boolean fallback) {
        return value instanceof Boolean bool ? bool : fallback;
    }

    private <T extends Enum<T>> T enumValue(
            Class<T> type,
            Object value,
            T fallback
    ) {
        if (!(value instanceof String name)) {
            return fallback;
        }

        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = database.getConnection();

        if (connection == null || connection.isClosed()) {
            throw new SQLException(
                    "Database is not connected"
            );
        }

        return connection;
    }
    }
