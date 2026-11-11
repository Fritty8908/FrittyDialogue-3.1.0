package fritty.dialogue.dialogue;

import fritty.dialogue.storage.DialogueRepository;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DialogueManager {

    private final DialogueRepository repository;

    private final Map<String, Dialogue> dialogues =
            new LinkedHashMap<>();

    public DialogueManager(DialogueRepository repository) {
        this.repository = repository;
    }

    public void loadAll() throws SQLException {
        dialogues.clear();

        for (Dialogue dialogue : repository.loadAll()) {
            dialogues.put(dialogue.getId(), dialogue);
        }
    }

    public Dialogue create(String name) throws SQLException {
        Dialogue dialogue = new Dialogue(name);

        repository.save(dialogue);
        dialogues.put(dialogue.getId(), dialogue);

        return dialogue;
    }

    public Dialogue get(String dialogueId) {
        return dialogues.get(dialogueId);
    }

    public boolean exists(String dialogueId) {
        return dialogues.containsKey(dialogueId);
    }

    public Collection<Dialogue> getAll() {
        return Collections.unmodifiableCollection(
                dialogues.values()
        );
    }

    public int getCount() {
        return dialogues.size();
    }

    public void save(Dialogue dialogue) throws SQLException {
        if (dialogue == null) {
            throw new IllegalArgumentException(
                    "Dialogue cannot be null"
            );
        }

        repository.save(dialogue);
        dialogues.put(dialogue.getId(), dialogue);
    }

    public boolean delete(String dialogueId) throws SQLException {
        if (!dialogues.containsKey(dialogueId)) {
            return false;
        }

        repository.delete(dialogueId);
        dialogues.remove(dialogueId);

        return true;
    }

    public void saveAll() throws SQLException {
        for (Dialogue dialogue : dialogues.values()) {
            repository.save(dialogue);
        }
    }
}
