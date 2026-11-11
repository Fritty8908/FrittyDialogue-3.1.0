package fritty.dialogue.conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Парсер логічних виразів умов діалогів.
 *
 * Підтримує:
 * - AND, OR, NOT;
 * - &&, ||, !;
 * - круглі дужки;
 * - значення у подвійних лапках;
 * - екранування символів;
 * - перевірку синтаксису;
 * - обчислення виразів;
 * - серіалізацію дерева;
 * - отримання списку умов.
 *
 * Приклад:
 *
 * (DIALOGUE:farmer AND BRANCH:quest_1)
 * OR
 * (PERMISSION:"dialogue.secret" AND NOT TAG:blocked)
 */
public final class DialogueConditionExpressionParser {

    private static final int MAX_EXPRESSION_LENGTH = 4096;
    private static final int MAX_TOKEN_COUNT = 512;
    private static final int MAX_PARSE_DEPTH = 128;

    private DialogueConditionExpressionParser() {
    }

    public enum TokenType {
        IDENTIFIER,
        STRING,
        AND,
        OR,
        NOT,
        LEFT_PAREN,
        RIGHT_PAREN,
        COLON,
        END
    }

    public enum NodeType {
        CONDITION,
        AND,
        OR,
        NOT
    }

    public record Token(
            TokenType type,
            String value,
            int position
    ) {
        public Token {
            Objects.requireNonNull(type);
            value = value == null ? "" : value;
        }
    }

    public record ParseError(
            String message,
            int position
    ) {
        public ParseError {
            message = message == null
                    ? "Невідома помилка"
                    : message;
            position = Math.max(0, position);
        }

        @Override
        public String toString() {
            return message + " (позиція " + position + ")";
        }
    }

    public record ParseResult(
            ExpressionNode root,
            List<ParseError> errors
    ) {
        public ParseResult {
            errors = errors == null
                    ? Collections.emptyList()
                    : List.copyOf(errors);
        }

        public boolean successful() {
            return root != null && errors.isEmpty();
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public String firstError() {
            return errors.isEmpty()
                    ? null
                    : errors.get(0).toString();
        }
    }

    public record ConditionReference(
            String type,
            String value
    ) {
        public ConditionReference {
            type = normalize(type);
            value = value == null ? "" : value.trim();

            if (type.isBlank()) {
                throw new IllegalArgumentException(
                        "Тип умови не може бути порожнім"
                );
            }

            if (value.isBlank()) {
                throw new IllegalArgumentException(
                        "Значення умови не може бути порожнім"
                );
            }
        }
    }

    public static final class ExpressionNode {

        private final NodeType type;
        private final ConditionReference condition;
        private final ExpressionNode left;
        private final ExpressionNode right;

        private ExpressionNode(
                NodeType type,
                ConditionReference condition,
                ExpressionNode left,
                ExpressionNode right
        ) {
            this.type = Objects.requireNonNull(type);
            this.condition = condition;
            this.left = left;
            this.right = right;

            validateStructure();
        }

        public static ExpressionNode condition(
                String type,
                String value
        ) {
            return new ExpressionNode(
                    NodeType.CONDITION,
                    new ConditionReference(type, value),
                    null,
                    null
            );
        }

        public static ExpressionNode condition(
                ConditionReference reference
        ) {
            return new ExpressionNode(
                    NodeType.CONDITION,
                    Objects.requireNonNull(reference),
                    null,
                    null
            );
        }

        public static ExpressionNode and(
                ExpressionNode left,
                ExpressionNode right
        ) {
            return new ExpressionNode(
                    NodeType.AND,
                    null,
                    Objects.requireNonNull(left),
                    Objects.requireNonNull(right)
            );
        }

        public static ExpressionNode or(
                ExpressionNode left,
                ExpressionNode right
        ) {
            return new ExpressionNode(
                    NodeType.OR,
                    null,
                    Objects.requireNonNull(left),
                    Objects.requireNonNull(right)
            );
        }

        public static ExpressionNode not(
                ExpressionNode child
        ) {
            return new ExpressionNode(
                    NodeType.NOT,
                    null,
                    Objects.requireNonNull(child),
                    null
            );
        }

        private void validateStructure() {
            switch (type) {
                case CONDITION -> {
                    if (condition == null
                            || left != null
                            || right != null) {
                        throw new IllegalArgumentException(
                                "Некоректний вузол умови"
                        );
                    }
                }

                case NOT -> {
                    if (condition != null
                            || left == null
                            || right != null) {
                        throw new IllegalArgumentException(
                                "Некоректний вузол NOT"
                        );
                    }
                }

                case AND, OR -> {
                    if (condition != null
                            || left == null
                            || right == null) {
                        throw new IllegalArgumentException(
                                "Некоректний логічний вузол"
                        );
                    }
                }
            }
        }

        public NodeType type() {
            return type;
        }

        public ConditionReference condition() {
            return condition;
        }

        public ExpressionNode left() {
            return left;
        }

        public ExpressionNode right() {
            return right;
        }

        public boolean isCondition() {
            return type == NodeType.CONDITION;
        }
    }

    /**
     * Розбиває вираз на токени.
     */
    public static List<Token> tokenize(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException(
                    "Вираз не може бути null"
            );
        }

        if (expression.length() > MAX_EXPRESSION_LENGTH) {
            throw new IllegalArgumentException(
                    "Вираз перевищує допустиму довжину"
            );
        }

        List<Token> tokens = new ArrayList<>();
        int index = 0;

        while (index < expression.length()) {
            char current = expression.charAt(index);

            if (Character.isWhitespace(current)) {
                index++;
                continue;
            }

            int start = index;

            if (current == '(') {
                tokens.add(new Token(
                        TokenType.LEFT_PAREN,
                        "(",
                        index++
                ));
                continue;
            }

            if (current == ')') {
                tokens.add(new Token(
                        TokenType.RIGHT_PAREN,
                        ")",
                        index++
                ));
                continue;
            }

            if (current == ':') {
                tokens.add(new Token(
                        TokenType.COLON,
                        ":",
                        index++
                ));
                continue;
            }

            if (current == '&'
                    && hasNext(expression, index, '&')) {
                tokens.add(new Token(
                        TokenType.AND,
                        "AND",
                        index
                ));
                index += 2;
                continue;
            }

            if (current == '|'
                    && hasNext(expression, index, '|')) {
                tokens.add(new Token(
                        TokenType.OR,
                        "OR",
                        index
                ));
                index += 2;
                continue;
            }

            if (current == '!') {
                tokens.add(new Token(
                        TokenType.NOT,
                        "NOT",
                        index++
                ));
                continue;
            }

            if (current == '"') {
                QuotedValue quoted = readQuoted(
                        expression,
                        index
                );

                tokens.add(new Token(
                        TokenType.STRING,
                        quoted.value(),
                        start
                ));

                index = quoted.nextIndex();
                continue;
            }

            if (isIdentifierCharacter(current)) {
                index++;

                while (index < expression.length()
                        && isIdentifierCharacter(
                                expression.charAt(index)
                        )) {
                    index++;
                }

                String value = expression.substring(start, index);
                String normalized = normalize(value);

                TokenType type = switch (normalized) {
                    case "AND" -> TokenType.AND;
                    case "OR" -> TokenType.OR;
                    case "NOT" -> TokenType.NOT;
                    default -> TokenType.IDENTIFIER;
                };

                tokens.add(new Token(
                        type,
                        value,
                        start
                ));

                continue;
            }

            throw new IllegalArgumentException(
                    "Неприпустимий символ '"
                            + current
                            + "' на позиції "
                            + index
            );
        }

        tokens.add(new Token(
                TokenType.END,
                "",
                expression.length()
        ));

        if (tokens.size() > MAX_TOKEN_COUNT) {
            throw new IllegalArgumentException(
                    "Вираз містить забагато токенів"
            );
        }

        return List.copyOf(tokens);
    }

    /**
     * Парсить вираз і повертає дерево або помилки.
     */
    public static ParseResult parse(String expression) {
        if (expression == null || expression.isBlank()) {
            return new ParseResult(
                    null,
                    List.of(new ParseError(
                            "Вираз умов порожній",
                            0
                    ))
            );
        }

        try {
            List<Token> tokens = tokenize(expression);
            Parser parser = new Parser(tokens, expression);

            ExpressionNode root = parser.parseExpression();

            return new ParseResult(
                    root,
                    Collections.emptyList()
            );

        } catch (ParserException exception) {
            return new ParseResult(
                    null,
                    List.of(new ParseError(
                            exception.getMessage(),
                            exception.position()
                    ))
            );

        } catch (IllegalArgumentException exception) {
            return new ParseResult(
                    null,
                    List.of(new ParseError(
                            exception.getMessage(),
                            0
                    ))
            );
        }
    }

    /**
     * Парсить вираз або викидає виняток.
     */
    public static ExpressionNode parseOrThrow(
            String expression
    ) {
        ParseResult result = parse(expression);

        if (!result.successful()) {
            throw new IllegalArgumentException(
                    result.firstError()
            );
        }

        return result.root();
    }

    /**
     * Обчислює логічний вираз.
     */
    public static boolean evaluate(
            ExpressionNode root,
            Predicate<ConditionReference> evaluator
    ) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(evaluator);

        return evaluateNode(root, evaluator);
    }

    private static boolean evaluateNode(
            ExpressionNode node,
            Predicate<ConditionReference> evaluator
    ) {
        return switch (node.type()) {
            case CONDITION ->
                    evaluator.test(node.condition());

            case AND ->
                    evaluateNode(node.left(), evaluator)
                            && evaluateNode(node.right(), evaluator);

            case OR ->
                    evaluateNode(node.left(), evaluator)
                            || evaluateNode(node.right(), evaluator);

            case NOT ->
                    !evaluateNode(node.left(), evaluator);
        };
    }

    /**
     * Повертає всі атомарні умови дерева.
     */
    public static List<ConditionReference> collectConditions(
            ExpressionNode root
    ) {
        Objects.requireNonNull(root);

        List<ConditionReference> result = new ArrayList<>();
        collect(root, result);

        return List.copyOf(result);
    }

    private static void collect(
            ExpressionNode node,
            List<ConditionReference> result
    ) {
        if (node.type() == NodeType.CONDITION) {
            result.add(node.condition());
            return;
        }

        if (node.left() != null) {
            collect(node.left(), result);
        }

        if (node.right() != null) {
            collect(node.right(), result);
        }
    }

    /**
     * Перевіряє наявність типу умови.
     */
    public static boolean containsType(
            ExpressionNode root,
            String type
    ) {
        if (root == null || type == null) {
            return false;
        }

        String expected = normalize(type);

        return collectConditions(root).stream()
                .anyMatch(condition ->
                        condition.type().equals(expected)
                );
    }

    /**
     * Повертає кількість атомарних умов.
     */
    public static int countConditions(ExpressionNode root) {
        return root == null
                ? 0
                : collectConditions(root).size();
    }

    /**
     * Обчислює глибину дерева.
     */
    public static int getDepth(ExpressionNode root) {
        if (root == null) {
            return 0;
        }

        if (root.type() == NodeType.CONDITION) {
            return 1;
        }

        int leftDepth = root.left() == null
                ? 0
                : getDepth(root.left());

        int rightDepth = root.right() == null
                ? 0
                : getDepth(root.right());

        return 1 + Math.max(leftDepth, rightDepth);
    }

    /**
     * Перевіряє обмеження складності.
     */
    public static boolean isWithinLimits(
            ExpressionNode root,
            int maxConditions,
            int maxDepth
    ) {
        if (root == null
                || maxConditions < 1
                || maxDepth < 1) {
            return false;
        }

        return countConditions(root) <= maxConditions
                && getDepth(root) <= maxDepth;
                }
    /**
     * Перетворює дерево назад у текстовий вираз.
     */
    public static String serialize(ExpressionNode root) {
        Objects.requireNonNull(root);
        return serializeNode(root, 0);
    }

    private static String serializeNode(
            ExpressionNode node,
            int parentPriority
    ) {
        int priority = priority(node.type());
        String result;

        switch (node.type()) {
            case CONDITION -> {
                result = node.condition().type()
                        + ":"
                        + quoteIfNeeded(
                                node.condition().value()
                        );
            }

            case NOT -> {
                result = "NOT "
                        + serializeNode(
                                node.left(),
                                priority
                        );
            }

            case AND -> {
                result = serializeNode(
                        node.left(),
                        priority
                )
                        + " AND "
                        + serializeNode(
                                node.right(),
                                priority
                        );
            }

            case OR -> {
                result = serializeNode(
                        node.left(),
                        priority
                )
                        + " OR "
                        + serializeNode(
                                node.right(),
                                priority
                        );
            }

            default -> throw new IllegalStateException(
                    "Невідомий тип вузла"
            );
        }

        if (priority < parentPriority) {
            return "(" + result + ")";
        }

        return result;
    }

    private static int priority(NodeType type) {
        return switch (type) {
            case CONDITION -> 4;
            case NOT -> 3;
            case AND -> 2;
            case OR -> 1;
        };
    }

    private static String quoteIfNeeded(String value) {
        if (value.matches("[\\p{L}\\p{N}_./%*\\-]+")) {
            return value;
        }

        return "\""
                + value.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\t", "\\t")
                        .replace("\r", "\\r")
                + "\"";
    }

    private static boolean hasNext(
            String input,
            int index,
            char expected
    ) {
        return index + 1 < input.length()
                && input.charAt(index + 1) == expected;
    }

    private static boolean isIdentifierCharacter(char value) {
        return Character.isLetterOrDigit(value)
                || value == '_'
                || value == '-'
                || value == '.'
                || value == '/'
                || value == '%'
                || value == '*';
    }

    private static QuotedValue readQuoted(
            String input,
            int start
    ) {
        StringBuilder result = new StringBuilder();

        int index = start + 1;
        boolean escaped = false;

        while (index < input.length()) {
            char current = input.charAt(index++);

            if (escaped) {
                switch (current) {
                    case 'n' -> result.append('\n');
                    case 't' -> result.append('\t');
                    case 'r' -> result.append('\r');
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    default -> {
                        result.append('\\');
                        result.append(current);
                    }
                }

                escaped = false;
                continue;
            }

            if (current == '\\') {
                escaped = true;
                continue;
            }

            if (current == '"') {
                return new QuotedValue(
                        result.toString(),
                        index
                );
            }

            result.append(current);
        }

        throw new ParserException(
                "Незакриті лапки",
                start
        );
    }

    private static String normalize(String value) {
        return value == null
                ? ""
                : value.trim().toUpperCase(Locale.ROOT);
    }

    private record QuotedValue(
            String value,
            int nextIndex
    ) {
    }

    /**
     * Внутрішня помилка синтаксичного аналізу.
     */
    private static final class ParserException
            extends IllegalArgumentException {

        private final int position;

        private ParserException(
                String message,
                int position
        ) {
            super(message);
            this.position = Math.max(0, position);
        }

        private int position() {
            return position;
        }
    }

    /**
     * Внутрішній синтаксичний аналізатор.
     */
    private static final class Parser {

        private final List<Token> tokens;
        private final String source;

        private int position;
        private int depth;

        private Parser(
                List<Token> tokens,
                String source
        ) {
            this.tokens = Objects.requireNonNull(tokens);
            this.source = Objects.requireNonNull(source);
        }

        private ExpressionNode parseExpression() {
            ExpressionNode result = parseOr();

            if (!check(TokenType.END)) {
                throw error(
                        "Зайвий токен після завершення виразу"
                );
            }

            return result;
        }

        /**
         * OR має найнижчий пріоритет.
         */
        private ExpressionNode parseOr() {
            ExpressionNode expression = parseAnd();

            while (match(TokenType.OR)) {
                ExpressionNode right = parseAnd();

                expression = ExpressionNode.or(
                        expression,
                        right
                );
            }

            return expression;
        }

        /**
         * AND має вищий пріоритет за OR.
         */
        private ExpressionNode parseAnd() {
            ExpressionNode expression = parseUnary();

            while (match(TokenType.AND)) {
                ExpressionNode right = parseUnary();

                expression = ExpressionNode.and(
                        expression,
                        right
                );
            }

            return expression;
        }

        /**
         * Обробляє унарний оператор NOT.
         */
        private ExpressionNode parseUnary() {
            if (match(TokenType.NOT)) {
                return ExpressionNode.not(parseUnary());
            }

            return parsePrimary();
        }

        /**
         * Обробляє дужки та атомарні умови.
         */
        private ExpressionNode parsePrimary() {
            if (++depth > MAX_PARSE_DEPTH) {
                depth--;

                throw error(
                        "Перевищено максимальну глибину виразу"
                );
            }

            try {
                if (match(TokenType.LEFT_PAREN)) {
                    ExpressionNode expression = parseOr();

                    if (!match(TokenType.RIGHT_PAREN)) {
                        throw error(
                                "Очікується закривальна дужка ')'"
                        );
                    }

                    return expression;
                }

                return parseCondition();

            } finally {
                depth--;
            }
        }

        /**
         * Читає умову у форматі TYPE:value.
         */
        private ExpressionNode parseCondition() {
            Token typeToken = peek();

            if (typeToken == null
                    || typeToken.type() != TokenType.IDENTIFIER) {
                throw error(
                        "Очікується тип умови"
                );
            }

            advance();

            if (!match(TokenType.COLON)) {
                throw error(
                        "Після типу умови очікується ':'"
                );
            }

            Token valueToken = peek();

            if (valueToken == null
                    || (valueToken.type() != TokenType.IDENTIFIER
                    && valueToken.type() != TokenType.STRING)) {
                throw error(
                        "Очікується значення умови"
                );
            }

            advance();

            try {
                return ExpressionNode.condition(
                        typeToken.value(),
                        valueToken.value()
                );

            } catch (IllegalArgumentException exception) {
                throw error(exception.getMessage());
            }
        }

        private boolean match(TokenType type) {
            if (!check(type)) {
                return false;
            }

            advance();
            return true;
        }

        private boolean check(TokenType type) {
            Token token = peek();

            return token != null
                    && token.type() == type;
        }

        private Token advance() {
            if (position < tokens.size()) {
                position++;
            }

            return previous();
        }

        private Token previous() {
            if (position <= 0
                    || position - 1 >= tokens.size()) {
                return null;
            }

            return tokens.get(position - 1);
        }

        private Token peek() {
            if (position >= tokens.size()) {
                return null;
            }

            return tokens.get(position);
        }

        private ParserException error(String message) {
            Token token = peek();

            int offset = token == null
                    ? source.length()
                    : token.position();

            return new ParserException(
                    message,
                    offset
            );
        }
    }
}
