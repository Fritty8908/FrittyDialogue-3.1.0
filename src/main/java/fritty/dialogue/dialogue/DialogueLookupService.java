package fritty.dialogue.dialogue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class DialogueLookupService {

    public Dialogue findById(
            DialogueManager manager,
            String dialogueId
    ) {
        Objects.requireNonNull(manager, "DialogueManager cannot be null");

        if (dialogueId == null || dialogueId.isBlank()) {
            return null;
        }

        return manager.get(dialogueId.trim());
    }

    public Branch findBranch(
            Dialogue dialogue,
            String branchId
    ) {
        Objects.requireNonNull(dialogue, "Dialogue cannot be null");

        if (branchId == null || branchId.isBlank()) {
            return null;
        }

        return dialogue.getBranches().get(branchId.trim());
    }

    public Answer findAnswer(
            Branch branch,
            String answerId
    ) {
        Objects.requireNonNull(branch, "Branch cannot be null");

        if (answerId == null || answerId.isBlank()) {
            return null;
        }

        return branch.getAnswers().stream()
                .filter(Objects::nonNull)
                .filter(answer -> answerId.trim().equals(answer.getId()))
                .findFirst()
                .orElse(null);
    }

    public List<Dialogue> search(
            DialogueManager manager,
            String query
    ) {
        Objects.requireNonNull(manager, "DialogueManager cannot be null");

        if (query == null || query.isBlank()) {
            return new ArrayList<>(manager.getAll());
        }

        String normalized = query.trim().toLowerCase(Locale.ROOT);

        return manager.getAll().stream()
                .filter(Objects::nonNull)
                .filter(dialogue ->
                        dialogue.getId().toLowerCase(Locale.ROOT)
                                .contains(normalized)
                        || dialogue.getName().toLowerCase(Locale.ROOT)
                                .contains(normalized)
                )
                .sorted(Comparator.comparing(
                        Dialogue::getName,
                        String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
    }
}
