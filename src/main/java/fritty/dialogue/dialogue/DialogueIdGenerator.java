package fritty.dialogue.dialogue;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class DialogueIdGenerator {

    private static final Map<Character, String> TRANSLITERATION = Map.ofEntries(
            Map.entry('а', "a"),
            Map.entry('б', "b"),
            Map.entry('в', "v"),
            Map.entry('г', "h"),
            Map.entry('ґ', "g"),
            Map.entry('д', "d"),
            Map.entry('е', "e"),
            Map.entry('є', "ye"),
            Map.entry('ж', "zh"),
            Map.entry('з', "z"),
            Map.entry('и', "y"),
            Map.entry('і', "i"),
            Map.entry('ї', "yi"),
            Map.entry('й', "y"),
            Map.entry('к', "k"),
            Map.entry('л', "l"),
            Map.entry('м', "m"),
            Map.entry('н', "n"),
            Map.entry('о', "o"),
            Map.entry('п', "p"),
            Map.entry('р', "r"),
            Map.entry('с', "s"),
            Map.entry('т', "t"),
            Map.entry('у', "u"),
            Map.entry('ф', "f"),
            Map.entry('х', "kh"),
            Map.entry('ц', "ts"),
            Map.entry('ч', "ch"),
            Map.entry('ш', "sh"),
            Map.entry('щ', "shch"),
            Map.entry('ь', ""),
            Map.entry('ю', "yu"),
            Map.entry('я', "ya"),
            Map.entry('ё', "yo"),
            Map.entry('ы', "y"),
            Map.entry('э', "e"),
            Map.entry('ъ', "")
    );

    private DialogueIdGenerator() {
    }

    public static String generate(String displayName) {
        Objects.requireNonNull(
                displayName,
                "Display name cannot be null"
        );

        String normalized = Normalizer.normalize(
                displayName.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD
        );

        StringBuilder result = new StringBuilder();
        boolean separator = false;

        for (char character : normalized.toCharArray()) {
            if (Character.getType(character)
                    == Character.NON_SPACING_MARK) {
                continue;
            }

            String replacement = TRANSLITERATION.get(
                    character
            );

            if (replacement != null) {
                appendToken(result, replacement);
                separator = false;
                continue;
            }

            if (character >= 'a' && character <= 'z'
                    || character >= '0' && character <= '9') {
                result.append(character);
                separator = false;
                continue;
            }

            if (!separator && !result.isEmpty()) {
                result.append('_');
                separator = true;
            }
        }

        while (!result.isEmpty()
                && result.charAt(result.length() - 1) == '_') {
            result.deleteCharAt(result.length() - 1);
        }

        if (result.isEmpty()) {
            return "dialogue";
        }

        return result.toString();
    }

    private static void appendToken(
            StringBuilder result,
            String token
    ) {
        result.append(token);
    }
}
