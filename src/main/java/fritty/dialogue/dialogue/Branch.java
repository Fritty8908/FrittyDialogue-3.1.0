package fritty.dialogue.dialogue;

import fritty.dialogue.conditions.ConditionGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Branch {

    private final String id;
    private String name;
    private String npcText;

    private final boolean root;
    private String nextBranchId;

    private final List<Answer> answers;
    private final DialogueSettings settings;

    private final ConditionGroup conditions;

    private String command;
    private CommandTiming commandTiming;

    public Branch(
            String id,
            String name,
            String npcText,
            boolean root
    ) {
        this.id = Objects.requireNonNull(
                id,
                "Branch ID cannot be null"
        );

        this.name = Objects.requireNonNull(
                name,
                "Branch name cannot be null"
        );

        this.npcText = npcText == null ? "" : npcText;
        this.root = root;

        this.answers = new ArrayList<>();
        this.settings = new DialogueSettings();

        this.conditions = new ConditionGroup(
                ConditionGroup.Operator.AND
        );

        this.command = null;
        this.commandTiming = CommandTiming.NONE;
    }

    public Branch(String name, boolean root) {
        this(
                UUID.randomUUID().toString(),
                name,
                "",
                root
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(
                name,
                "Branch name cannot be null"
        );
    }

    public String getNpcText() {
        return npcText;
    }

    public void setNpcText(String npcText) {
        this.npcText = npcText == null ? "" : npcText;
    }

    public boolean isRoot() {
        return root;
    }

    public String getNextBranchId() {
        return nextBranchId;
    }

    public void setNextBranchId(String nextBranchId) {
        this.nextBranchId = nextBranchId;
    }

    public boolean hasNextBranch() {
        return nextBranchId != null
                && !nextBranchId.isBlank();
    }

    public List<Answer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public void addAnswer(Answer answer) {
        Objects.requireNonNull(
                answer,
                "Answer cannot be null"
        );

        if (getAnswer(answer.getId()) != null) {
            throw new IllegalArgumentException(
                    "Answer with this ID already exists"
            );
        }

        answers.add(answer);
    }

    public boolean removeAnswer(String answerId) {
        return answers.removeIf(
                answer -> answer.getId().equals(answerId)
        );
    }

    public Answer getAnswer(String answerId) {
        return answers.stream()
                .filter(answer -> answer.getId().equals(answerId))
                .findFirst()
                .orElse(null);
    }

    public boolean hasAnswers() {
        return !answers.isEmpty();
    }

    public DialogueSettings getSettings() {
        return settings;
    }

    public Object getSetting(
            SettingKey key,
            Dialogue dialogue
    ) {
        Objects.requireNonNull(
                key,
                "Setting key cannot be null"
        );

        if (settings.contains(key.getKey())) {
            return settings.get(key.getKey());
        }

        if (dialogue != null
                && dialogue.getSettings().contains(key.getKey())) {
            return dialogue.getSettings().get(key.getKey());
        }

        return key.getDefaultValue();
    }

    public boolean hasSettingOverride(SettingKey key) {
        return settings.contains(key.getKey());
    }

    public void clearSettingOverride(SettingKey key) {
        settings.remove(key.getKey());
    }

    public ConditionGroup getConditions() {
        return conditions;
    }

    public boolean hasConditions() {
        return !conditions.isEmpty();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command == null || command.isBlank()
                ? null
                : command;
    }

    public boolean hasCommand() {
        return command != null && !command.isBlank();
    }

    public CommandTiming getCommandTiming() {
        return commandTiming;
    }

    public void setCommandTiming(CommandTiming commandTiming) {
        this.commandTiming = Objects.requireNonNull(
                commandTiming,
                "Command timing cannot be null"
        );
    }

    public enum CommandTiming {

        NONE,
        ON_START,
        AFTER_TEXT
    }
}
