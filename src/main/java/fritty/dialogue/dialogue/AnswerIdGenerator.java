package fritty.dialogue.dialogue;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public final class AnswerIdGenerator {

    private AnswerIdGenerator() {
    }

    public static String generate(
            String answerText,
            Iterable<Answer> existingAnswers
    ) {
        Objects.requireNonNull(
                existingAnswers,
                "Existing answers cannot be null"
        );

        if (answerText == null || answerText.isBlank()) {
            throw new IllegalArgumentException(
                    "Текст відповіді не може бути порожнім."
            );
        }

        Set<String> existingIds = new HashSet<>();

        for (Answer answer : existingAnswers) {
            if (answer != null && answer.getId() != null) {
                existingIds.add(answer.getId());
            }
        }

        String baseId = createBaseId(answerText);
        String candidate = baseId;
        int suffix = 2;

        while (existingIds.contains(candidate)) {
            candidate = baseId + "_" + suffix;
            suffix++;
        }

        return candidate;
    }

    private static String createBaseId(String text) {
        String normalized = Normalizer.normalize(
                text.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );

        StringBuilder result = new StringBuilder();
        boolean separator = false;

        for (char character : normalized.toCharArray()) {
            if (Character.getType(character)
                    == Character.NON_SPACING_MARK) {
                continue;
            }

            if (character >= 'a' && character <= 'z'
                    || character >= '0' && character <= '9') {
                result.append(character);
                separator = false;
            } else if (!separator && !result.isEmpty()) {
                result.append('_');
                separator = true;
            }
        }

        while (!result.isEmpty()
                && result.charAt(result.length() - 1) == '_') {
            result.deleteCharAt(result.length() - 1);
        }

        if (result.isEmpty()) {
            return "answer";
        }

        if (result.length() > 48) {
            return result.substring(0, 48);
        }

        return result.toString();
    }
}
