package fritty.dialogue.conditions;

import fritty.dialogue.progress.ProgressManager;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Центральний механізм перевірки умов діалогів.
 *
 * Підтримує:
 * - виконані діалоги;
 * - виконані гілки;
 * - вибрані відповіді;
 * - дозволи Bukkit / LuckPerms;
 * - scoreboard-теги;
 * - групи AND / OR;
 * - вкладені групи;
 * - повідомлення про невиконані умови.
 *
 * Формат умови визначається класом Condition.
 */
public final class DialogueConditionEngine {

    private final ProgressManager progressManager;

    private final Map<UUID, ConditionResult> lastResults =
            new ConcurrentHashMap<>();

    public DialogueConditionEngine(
            ProgressManager progressManager
    ) {
        this.progressManager = Objects.requireNonNull(progressManager);
    }

    /**
     * Перевіряє одну умову.
     */
    public ConditionResult evaluate(
            Player player,
            Condition condition
    ) throws SQLException {
        Objects.requireNonNull(player);
        Objects.requireNonNull(condition);

        String type = readType(condition);
        String dialogueId = readDialogueId(condition);
        String branchId = readBranchId(condition);
        String answerId = readAnswerId(condition);
        String value = readValue(condition);

        boolean passed;

        switch (type) {
            case "DIALOGUE_COMPLETED":
            case "COMPLETED_DIALOGUE":
            case "DIALOGUE":
                passed = checkDialogue(
                        player,
                        dialogueId
                );
                break;

            case "BRANCH_COMPLETED":
            case "COMPLETED_BRANCH":
            case "BRANCH":
                passed = checkBranch(
                        player,
                        dialogueId,
                        branchId
                );
                break;

            case "ANSWER_SELECTED":
            case "SELECTED_ANSWER":
            case "ANSWER":
                passed = checkAnswer(
                        player,
                        dialogueId,
                        branchId,
                        answerId
                );
                break;

            case "PERMISSION":
            case "HAS_PERMISSION":
                passed = checkPermission(player, value);
                break;

            case "SCOREBOARD_TAG":
            case "TAG":
            case "HAS_TAG":
                passed = checkTag(player, value);
                break;

            default:
                passed = false;
                break;
        }

        return new ConditionResult(
                passed,
                type,
                buildDescription(
                        type,
                        dialogueId,
                        branchId,
                        answerId,
                        value
                )
        );
    }

    /**
     * Перевіряє групу умов.
     */
    public ConditionResult evaluateGroup(
            Player player,
            ConditionGroup group
    ) throws SQLException {
        Objects.requireNonNull(player);
        Objects.requireNonNull(group);

        List<ConditionResult> results = new ArrayList<>();

        for (Condition condition : group.getConditions()) {
            results.add(evaluate(player, condition));
        }

        boolean passed = evaluateGroupResult(
                group,
                results
        );

        List<String> failed = new ArrayList<>();

        for (ConditionResult result : results) {
            if (!result.passed()) {
                failed.add(result.description());
            }
        }

        return new ConditionResult(
                passed,
                "GROUP",
                passed
                        ? "Усі необхідні умови виконані."
                        : String.join(", ", failed)
        );
    }

    /**
     * Перевіряє вкладену групу з підтримкою AND / OR.
     */
    public ConditionResult evaluateNested(
            Player player,
            ConditionNode node
    ) throws SQLException {
        Objects.requireNonNull(player);
        Objects.requireNonNull(node);

        if (node.condition() != null) {
            return evaluate(player, node.condition());
        }

        List<ConditionResult> results = new ArrayList<>();

        for (ConditionNode child : node.children()) {
            results.add(evaluateNested(player, child));
        }

        boolean passed = node.operator() == ConditionOperator.AND
                ? results.stream().allMatch(ConditionResult::passed)
                : results.stream().anyMatch(ConditionResult::passed);

        List<String> failed = new ArrayList<>();

        for (ConditionResult result : results) {
            if (!result.passed()) {
                failed.add(result.description());
            }
        }

        return new ConditionResult(
                passed,
                "NESTED_GROUP",
                passed
                        ? "Умови групи виконані."
                        : String.join(", ", failed)
        );
    }

    /**
     * Перевіряє завершення діалогу.
     */
    public boolean checkDialogue(
            Player player,
            String dialogueId
    ) throws SQLException {
        if (!validId(dialogueId)) {
            return false;
        }

        return progressManager.hasCompletedDialogue(
                player.getUniqueId(),
                dialogueId
        );
    }

    /**
     * Перевіряє завершення гілки.
     */
    public boolean checkBranch(
            Player player,
            String dialogueId,
            String branchId
    ) throws SQLException {
        if (!validId(dialogueId) || !validId(branchId)) {
            return false;
        }

        return progressManager.hasCompletedBranch(
                player.getUniqueId(),
                dialogueId,
                branchId
        );
    }

    /**
     * Перевіряє вибір відповіді.
     */
    public boolean checkAnswer(
            Player player,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {
        if (!validId(dialogueId)
                || !validId(branchId)
                || !validId(answerId)) {
            return false;
        }

        return progressManager.hasSelectedAnswer(
                player.getUniqueId(),
                dialogueId,
                branchId,
                answerId
        );
    }

    /**
     * Перевіряє дозвіл через Bukkit Permission API.
     *
     * LuckPerms автоматично враховується через Bukkit API,
     * якщо його інтеграція з сервером активна.
     */
    public boolean checkPermission(
            Player player,
            String permission
    ) {
        if (!validId(permission)) {
            return false;
        }

        return player.hasPermission(permission);
    }

    /**
     * Перевіряє scoreboard-тег.
     *
     * Теги не записуються в базу прогресу.
     */
    public boolean checkTag(
            Player player,
            String tag
    ) {
        if (!validId(tag)) {
            return false;
        }

        return player.getScoreboardTags().contains(tag);
    }

    /**
     * Перевіряє AND / OR для звичайної групи.
     */
    private boolean evaluateGroupResult(
            ConditionGroup group,
            List<ConditionResult> results
    ) {
        if (results.isEmpty()) {
            return true;
        }

        String operator = readOperator(group);

        if ("OR".equalsIgnoreCase(operator)) {
            return results.stream().anyMatch(
                    ConditionResult::passed
            );
        }

        return results.stream().allMatch(
                ConditionResult::passed
        );
    }

    /**
     * Перевіряє умову доступності діалогу
     * та зберігає результат для подальшого відображення.
     */
    public ConditionResult checkAvailability(
            Player player,
            ConditionGroup group
    ) throws SQLException {
        ConditionResult result = evaluateGroup(
                player,
                group
        );

        lastResults.put(
                player.getUniqueId(),
                result
        );

        return result;
    }

    /**
     * Перевіряє доступність за вкладеною структурою.
     */
    public ConditionResult checkAvailability(
            Player player,
            ConditionNode node
    ) throws SQLException {
        ConditionResult result = evaluateNested(
                player,
                node
        );

        lastResults.put(
                player.getUniqueId(),
                result
        );

        return result;
    }

    /**
     * Повертає останній результат перевірки.
     */
    public ConditionResult getLastResult(Player player) {
        Objects.requireNonNull(player);

        return lastResults.get(
                player.getUniqueId()
        );
    }

    /**
     * Повертає причину недоступності.
     */
    public String getFailureMessage(Player player) {
        ConditionResult result = getLastResult(player);

        if (result == null || result.passed()) {
            return null;
        }

        return result.description();
    }

    /**
     * Перевіряє, чи можна почати діалог.
     */
    public boolean canStart(
            Player player,
            ConditionGroup group
    ) throws SQLException {
        return checkAvailability(player, group).passed();
    }

    /**
     * Перевіряє, чи можна перейти до відповіді.
     */
    public boolean canSelectAnswer(
            Player player,
            ConditionGroup requirements
    ) throws SQLException {
        return checkAvailability(
                player,
                requirements
        ).passed();
    }

    /**
     * Видаляє кеш результатів гравця.
     */
    public void clear(UUID playerId) {
        if (playerId != null) {
            lastResults.remove(playerId);
        }
    }

    /**
     * Очищає весь кеш.
     */
    public void clearAll() {
        lastResults.clear();
    }

    private String readType(Condition condition) {
        return normalize(condition.getType());
    }

    private String readDialogueId(Condition condition) {
        return safe(condition.getDialogueId());
    }

    private String readBranchId(Condition condition) {
        return safe(condition.getBranchId());
    }

    private String readAnswerId(Condition condition) {
        return safe(condition.getAnswerId());
    }

    private String readValue(Condition condition) {
        return safe(condition.getValue());
    }

    private String readOperator(ConditionGroup group) {
        return safe(group.getOperator());
    }

    private String buildDescription(
            String type,
            String dialogueId,
            String branchId,
            String answerId,
            String value
    ) {
        switch (type) {
            case "DIALOGUE_COMPLETED":
            case "COMPLETED_DIALOGUE":
            case "DIALOGUE":
                return "Не завершено діалог: " + dialogueId;

            case "BRANCH_COMPLETED":
            case "COMPLETED_BRANCH":
            case "BRANCH":
                return "Не завершено гілку: "
                        + dialogueId + "/" + branchId;

            case "ANSWER_SELECTED":
            case "SELECTED_ANSWER":
            case "ANSWER":
                return "Не вибрано відповідь: "
                        + dialogueId + "/" + branchId
                        + "/" + answerId;

            case "PERMISSION":
            case "HAS_PERMISSION":
                return "Відсутній дозвіл: " + value;

            case "SCOREBOARD_TAG":
            case "TAG":
            case "HAS_TAG":
                return "Відсутній scoreboard-тег: " + value;

            default:
                return "Невідома або некоректна умова: " + type;
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean validId(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Результат перевірки умови.
     */
    public record ConditionResult(
            boolean passed,
            String type,
            String description
    ) {
    }

    /**
     * Логічний оператор групи.
     */
    public enum ConditionOperator {
        AND,
        OR
    }

    /**
     * Вузол дерева умов.
     *
     * Вузол містить або одну умову,
     * або список дочірніх вузлів.
     */
    public record ConditionNode(
            Condition condition,
            ConditionOperator operator,
            List<ConditionNode> children
    ) {
        public ConditionNode {
            children = children == null
                    ? Collections.emptyList()
                    : List.copyOf(children);

            operator = operator == null
                    ? ConditionOperator.AND
                    : operator;

            if (condition != null && !children.isEmpty()) {
                throw new IllegalArgumentException(
                        "Node cannot contain condition and children together"
                );
            }
        }

        public static ConditionNode condition(Condition condition) {
            return new ConditionNode(
                    Objects.requireNonNull(condition),
                    ConditionOperator.AND,
                    Collections.emptyList()
            );
        }

        public static ConditionNode group(
                ConditionOperator operator,
                List<ConditionNode> children
        ) {
            return new ConditionNode(
                    null,
                    operator,
                    children
            );
        }
    }
}
