package fritty.dialogue.conditions;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Єдина точка отримання актуального контексту умов гравця.
 */
public final class DialogueConditionExpressionContextProvider {

    private final DialogueConditionExpressionContextFactory factory;
    private final DialogueConditionExpressionContextCache cache;
    private final DialogueConditionExpressionContextSanitizer sanitizer;
    private final DialogueConditionExpressionContextValidator validator;

    public DialogueConditionExpressionContextProvider() {
        this(
                new DialogueConditionExpressionContextFactory(),
                new DialogueConditionExpressionContextCache(),
                new DialogueConditionExpressionContextSanitizer(),
                new DialogueConditionExpressionContextValidator()
        );
    }

    public DialogueConditionExpressionContextProvider(
            DialogueConditionExpressionContextFactory factory,
            DialogueConditionExpressionContextCache cache,
            DialogueConditionExpressionContextSanitizer sanitizer,
            DialogueConditionExpressionContextValidator validator
    ) {
        this.factory = Objects.requireNonNull(factory);
        this.cache = Objects.requireNonNull(cache);
        this.sanitizer = Objects.requireNonNull(sanitizer);
        this.validator = Objects.requireNonNull(validator);
    }

    public DialogueConditionExpressionContext get(Player player) {
        return get(player, Map.of(), false);
    }

    public DialogueConditionExpressionContext get(
            Player player,
            Map<String, ?> additionalVariables
    ) {
        return get(player, additionalVariables, false);
    }

    public DialogueConditionExpressionContext refresh(Player player) {
        return get(player, Map.of(), true);
    }

    public DialogueConditionExpressionContext get(
            Player player,
            Map<String, ?> additionalVariables,
            boolean forceRefresh
    ) {
        Objects.requireNonNull(player, "Player cannot be null");

        UUID playerId = player.getUniqueId();

        if (!forceRefresh && (additionalVariables == null
                || additionalVariables.isEmpty())) {
            DialogueConditionExpressionContext cached =
                    cache.get(playerId);

            if (cached != null) {
                return cached;
            }
        }

        DialogueConditionExpressionContext context =
                factory.create(player, additionalVariables);

        context = sanitizer.sanitize(context);

        DialogueConditionExpressionContextValidator.ValidationResult result =
                validator.validate(context);

        if (!result.valid()) {
            throw new IllegalArgumentException(
                    "Invalid player condition context: "
                            + result.firstError()
            );
        }

        if (additionalVariables == null
                || additionalVariables.isEmpty()) {
            cache.put(context);
        }

        return context;
    }

    public void invalidate(UUID playerId) {
        cache.invalidate(playerId);
    }

    public void clear() {
        cache.clear();
    }

    public int getCachedPlayerCount() {
        return cache.size();
    }
}
