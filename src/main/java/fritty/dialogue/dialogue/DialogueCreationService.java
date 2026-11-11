package fritty.dialogue.dialogue;

import java.util.Locale;
import java.util.Objects;

public final class DialogueCreationService {

    private final DialogueManager dialogueManager;

    public DialogueCreationService(DialogueManager dialogueManager) {
        this.dialogueManager = Objects.requireNonNull(
                dialogueManager,
                "Dialogue manager cannot be null"
        );
    }

    public Dialogue create(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException(
                    "Назва діалогу не може бути порожньою."
            );
        }

        String name = displayName.trim();
        String baseId = DialogueIdGenerator.generate(name);
        String id = createUniqueId(baseId);

        return dialogueManager.createWithId(id, name);
    }

    private String createUniqueId(String baseId) {
        String normalized = DialogueIdValidator.validate(baseId);

        if (!dialogueManager.exists(normalized)) {
            return normalized;
        }

        int suffix = 2;

        while (true) {
            String candidate = normalized + "_" + suffix;

            if (!dialogueManager.exists(candidate)) {
                return candidate;
            }

            suffix++;
        }
    }
}
