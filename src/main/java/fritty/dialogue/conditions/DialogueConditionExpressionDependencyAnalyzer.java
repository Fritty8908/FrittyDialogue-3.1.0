package fritty.dialogue.conditions;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Аналізує залежності логічного виразу умов.
 *
 * Визначає, які типи умов використовуються,
 * та повертає унікальні посилання на них.
 */
public final class DialogueConditionExpressionDependencyAnalyzer {

    private final DialogueConditionExpressionService expressionService;

    public DialogueConditionExpressionDependencyAnalyzer() {
        this(new DialogueConditionExpressionService());
    }

    public DialogueConditionExpressionDependencyAnalyzer(
            DialogueConditionExpressionService expressionService
    ) {
        this.expressionService = Objects.requireNonNull(
                expressionService,
                "Expression service cannot be null"
        );
    }

    /**
     * Повний аналіз залежностей виразу.
     */
    public Analysis analyze(String expression) {
        DialogueConditionExpressionService.ExpressionAnalysis parsed =
                expressionService.analyze(expression);

        if (!parsed.successful()) {
            return Analysis.failure(
                    parsed.error(),
                    parsed.errorPosition()
            );
        }

        Set<String> types = new LinkedHashSet<>();
        Set<String> permissions = new LinkedHashSet<>();
        Set<String> tags = new LinkedHashSet<>();

        List<Dependency> dependencies = new ArrayList<>();

        for (DialogueConditionExpressionParser.ConditionReference reference
                : parsed.conditions()) {

            String type = normalize(reference.type());
            String value = reference.value();

            types.add(type);

            if (type.equals("LUCKPERMS_PERMISSION")) {
                permissions.add(value);
            }

            if (type.equals("PLAYER_TAG")) {
                tags.add(value);
            }

            dependencies.add(new Dependency(type, value));
        }

        return Analysis.success(
                parsed.normalizedExpression(),
                dependencies,
                types,
                permissions,
                tags
        );
    }

    public boolean usesType(
            String expression,
            String type
    ) {
        if (type == null || type.isBlank()) {
            return false;
        }

        return expressionService.containsConditionType(
                expression,
                type.trim()
        );
    }

    public List<Dependency> getDependencies(String expression) {
        Analysis analysis = analyze(expression);

        return analysis.successful()
                ? analysis.dependencies()
                : List.of();
    }

    private static String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    public record Dependency(
            String type,
            String value
    ) {
        public Dependency {
            if (type == null || type.isBlank()) {
                throw new IllegalArgumentException(
                        "Dependency type cannot be empty"
                );
            }

            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException(
                        "Dependency value cannot be empty"
                );
            }

            type = normalize(type);
            value = value.trim();
        }
    }

    public record Analysis(
            boolean successful,
            String normalizedExpression,
            List<Dependency> dependencies,
            Set<String> conditionTypes,
            Set<String> permissions,
            Set<String> playerTags,
            String error,
            int errorPosition
    ) {
        public Analysis {
            normalizedExpression = normalizedExpression == null
                    ? ""
                    : normalizedExpression;

            dependencies = List.copyOf(dependencies);
            conditionTypes = Set.copyOf(conditionTypes);
            permissions = Set.copyOf(permissions);
            playerTags = Set.copyOf(playerTags);

            if (successful) {
                error = null;
                errorPosition = -1;
            }
        }

        private static Analysis success(
                String expression,
                List<Dependency> dependencies,
                Set<String> types,
                Set<String> permissions,
                Set<String> tags
        ) {
            return new Analysis(
                    true,
                    expression,
                    dependencies,
                    types,
                    permissions,
                    tags,
                    null,
                    -1
            );
        }

        private static Analysis failure(
                String error,
                int position
        ) {
            return new Analysis(
                    false,
                    "",
                    List.of(),
                    Set.of(),
                    Set.of(),
                    Set.of(),
                    error,
                    Math.max(0, position)
            );
        }
    }
}
