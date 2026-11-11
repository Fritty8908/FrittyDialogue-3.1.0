package fritty.dialogue.conditions;

import java.util.Objects;

/**
 * Форматує логічні вирази умов для відображення
 * в редакторі діалогів та діагностичних повідомленнях.
 */
public final class DialogueConditionExpressionFormatter {

    private static final String INDENT = "    ";

    public String format(String expression) {
        DialogueConditionExpressionParser.ParseResult result =
                DialogueConditionExpressionParser.parse(expression);

        if (!result.successful()) {
            return "Некоректний вираз: " + result.firstError();
        }

        return format(result.root());
    }

    public String format(
            DialogueConditionExpressionParser.ExpressionNode root
    ) {
        Objects.requireNonNull(root);

        StringBuilder output = new StringBuilder();
        appendNode(root, output, 0, "ВИРАЗ");

        return output.toString();
    }

    public String formatCompact(String expression) {
        DialogueConditionExpressionParser.ParseResult result =
                DialogueConditionExpressionParser.parse(expression);

        if (!result.successful()) {
            return null;
        }

        return DialogueConditionExpressionParser.serialize(
                result.root()
        );
    }

    public String formatForChat(String expression) {
        DialogueConditionExpressionParser.ParseResult result =
                DialogueConditionExpressionParser.parse(expression);

        if (!result.successful()) {
            return "§cНекоректний вираз умов.";
        }

        return formatChatNode(result.root(), 0);
    }

    private void appendNode(
            DialogueConditionExpressionParser.ExpressionNode node,
            StringBuilder output,
            int depth,
            String label
    ) {
        appendIndent(output, depth);
        output.append(label).append(": ");

        switch (node.type()) {
            case CONDITION -> {
                output.append(node.condition().type())
                        .append(" = ")
                        .append('"')
                        .append(node.condition().value())
                        .append('"')
                        .append('\n');
            }

            case NOT -> {
                output.append("NOT\n");

                appendNode(
                        node.left(),
                        output,
                        depth + 1,
                        "УМОВА"
                );
            }

            case AND, OR -> {
                output.append(node.type().name())
                        .append('\n');

                appendNode(
                        node.left(),
                        output,
                        depth + 1,
                        "ЛІВА ЧАСТИНА"
                );

                appendNode(
                        node.right(),
                        output,
                        depth + 1,
                        "ПРАВА ЧАСТИНА"
                );
            }
        }
    }

    private String formatChatNode(
            DialogueConditionExpressionParser.ExpressionNode node,
            int depth
    ) {
        String indent = "  ".repeat(depth);

        return switch (node.type()) {
            case CONDITION ->
                    indent
                            + "§f"
                            + node.condition().type()
                            + "§7: §e"
                            + node.condition().value();

            case NOT ->
                    indent
                            + "§cNOT\n"
                            + formatChatNode(node.left(), depth + 1);

            case AND, OR ->
                    indent
                            + (node.type() == DialogueConditionExpressionParser.NodeType.AND
                            ? "§aAND"
                            : "§6OR")
                            + "\n"
                            + formatChatNode(node.left(), depth + 1)
                            + "\n"
                            + formatChatNode(node.right(), depth + 1);
        };
    }

    private void appendIndent(
            StringBuilder output,
            int depth
    ) {
        output.append(INDENT.repeat(Math.max(0, depth)));
    }
}
