package fritty.dialogue.dialogue;

import java.util.Objects;

public final class DialogueRenameService {

    public void rename(Dialogue dialogue, String newName) {
        Objects.requireNonNull(
                dialogue,
                "Dialogue cannot be null"
        );

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException(
                    "Нова назва діалогу не може бути порожньою."
            );
        }

        dialogue.setName(newName.trim());
    }
}
