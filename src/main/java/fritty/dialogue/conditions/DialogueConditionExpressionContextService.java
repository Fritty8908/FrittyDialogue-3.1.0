package fritty.dialogue.conditions;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Сервіс керування контекстами виразів умов.
 */
public final class DialogueConditionExpressionContextService {

    private final DialogueConditionExpressionContextProvider provider;
    private final DialogueConditionExpressionVariableResolver resolver;
    private final DialogueConditionExpressionContextMerger merger;

    public DialogueConditionExpressionContextService() {
        this(
                new DialogueConditionExpressionContextProvider(),
                new DialogueConditionExpressionVariableResolver(),
                new DialogueConditionExpressionContextMerger()
        );
    }

    public DialogueConditionExpressionContextService(
            DialogueConditionExpressionContextProvider provider,
            DialogueConditionExpressionVariableResolver resolver,
            DialogueConditionExpressionContextMerger merger
    ) {
        this.provider = Objects.requireNonNull(provider);
        this.resolver = Objects.requireNonNull(resolver);
        this.merger = Objects.requireNonNull(merger);
    }

    public DialogueConditionExpressionContext get(Player player) {
        return provider.get(player);
    }

    public DialogueConditionExpressionContext get(
            Player player,
            Map<String, ?> variables
    ) {
        return provider.get(player, variables);
    }

    public DialogueConditionExpressionContext refresh(Player player) {
        return provider.refresh(player);
    }

    public DialogueConditionExpressionContext merge(
            DialogueConditionExpressionContext context,
            Map<String, ?> variables
    ) {
        return merger.merge(context, variables);
    }

    public DialogueConditionExpressionVariableResolver.ResolutionResult
    resolveVariables(
            String expression,
            DialogueConditionExpressionContext context
    ) {
        return resolver.resolve(expression, context);
    }

    public DialogueConditionExpressionVariableResolver.ResolutionResult
    resolveVariables(
            String expression,
            Player player
    ) {
        return resolver.resolve(
                expression,
                provider.get(player)
        );
    }

    public void invalidate(UUID playerId) {
        provider.invalidate(playerId);
    }

    public void clear() {
        provider.clear();
    }

    public int getCachedPlayerCount() {
        return provider.getCachedPlayerCount();
    }

    public boolean containsVariables(String expression) {
        return resolver.containsVariables(expression);
    }
}
