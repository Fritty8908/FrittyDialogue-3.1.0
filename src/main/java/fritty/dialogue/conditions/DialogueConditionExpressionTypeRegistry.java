package fritty.dialogue.conditions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Реєстр типів умов та їхніх альтернативних назв.
 *
 * Дозволяє використовувати короткі й повні назви
 * типів умов у логічних виразах.
 */
public final class DialogueConditionExpressionTypeRegistry {

    private static final Map<String, Condition.Type> TYPES =
            createTypes();

    private DialogueConditionExpressionTypeRegistry() {
    }

    private static Map<String, Condition.Type> createTypes() {
        Map<String, Condition.Type> types = new LinkedHashMap<>();

        register(
                types,
                Condition.Type.COMPLETED_DIALOGUE,
                "DIALOGUE",
                "DIALOGUE_COMPLETED",
                "COMPLETED_DIALOGUE"
        );

        register(
                types,
                Condition.Type.COMPLETED_BRANCH,
                "BRANCH",
                "BRANCH_COMPLETED",
                "COMPLETED_BRANCH"
        );

        register(
                types,
                Condition.Type.SELECTED_ANSWER,
                "ANSWER",
                "ANSWER_SELECTED",
                "SELECTED_ANSWER"
        );

        register(
                types,
                Condition.Type.LUCKPERMS_PERMISSION,
                "PERMISSION",
                "HAS_PERMISSION",
                "LUCKPERMS_PERMISSION"
        );

        register(
                types,
                Condition.Type.PLAYER_TAG,
                "TAG",
                "HAS_TAG",
                "SCOREBOARD_TAG",
                "PLAYER_TAG"
        );

        return Collections.unmodifiableMap(types);
    }

    private static void register(
            Map<String, Condition.Type> types,
            Condition.Type type,
            String... aliases
    ) {
        for (String alias : aliases) {
            String normalized = normalize(alias);

            Condition.Type previous = types.put(
                    normalized,
                    type
            );

            if (previous != null && previous != type) {
                throw new IllegalStateException(
                        "Duplicate condition alias: " + normalized
                );
            }
        }

        types.put(type.name(), type);
    }

    /**
     * Знаходить тип умови за назвою або псевдонімом.
     */
    public static Optional<Condition.Type> find(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                TYPES.get(normalize(name))
        );
    }

    /**
     * Перетворює назву на тип умови.
     *
     * Якщо тип невідомий, кидає виняток.
     */
    public static Condition.Type resolve(String name) {
        return find(name).orElseThrow(
                () -> new IllegalArgumentException(
                        "Невідомий тип умови: " + name
                )
        );
    }

    /**
     * Перевіряє, чи підтримується назва типу.
     */
    public static boolean isSupported(String name) {
        return find(name).isPresent();
    }

    /**
     * Повертає всі зареєстровані назви.
     */
    public static Set<String> getRegisteredNames() {
        return TYPES.keySet();
    }

    /**
     * Повертає канонічну назву типу.
     */
    public static String getCanonicalName(String name) {
        return resolve(name).name();
    }

    /**
     * Повертає незмінну таблицю псевдонімів.
     */
    public static Map<String, Condition.Type> getAll() {
        return TYPES;
    }

    private static String normalize(String name) {
        return name.trim().toUpperCase(Locale.ROOT);
    }
}
