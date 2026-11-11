package fritty.dialogue.dialogue;

import fritty.dialogue.storage.DialogueRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class DialogueManager {

    private final DialogueRepository repository;

    private final Map<String, Dialogue> dialogues =
            new LinkedHashMap<>();

    public DialogueManager(DialogueRepository repository) {
        this.repository = Objects.requireNonNull(
                repository,
                "Dialogue repository cannot be null"
        );
    }

    public synchronized void loadAll() throws SQLException {
        Map<String, Dialogue> loaded = new LinkedHashMap<>();

        for (Dialogue dialogue : repository.loadAll()) {
            loaded.put(dialogue.getId(), dialogue);
        }

        dialogues.clear();
        dialogues.putAll(loaded);
    }

    public synchronized void reload() throws SQLException {
        loadAll();
    }

    public synchronized boolean reload(String dialogueId)
            throws SQLException {

        Dialogue dialogue = repository.load(dialogueId);

        if (dialogue == null) {
            return dialogues.remove(dialogueId) != null;
        }

        dialogues.put(dialogueId, dialogue);
        return true;
    }

    public synchronized Dialogue create(String name) {
        Dialogue dialogue = new Dialogue(name);

        dialogues.put(dialogue.getId(), dialogue);

        return dialogue;
    }

    public synchronized Dialogue get(String dialogueId) {
        return dialogues.get(dialogueId);
    }

    public synchronized boolean exists(String dialogueId) {
        return dialogues.containsKey(dialogueId);
    }

    public synchronized Collection<Dialogue> getAll() {
        return Collections.unmodifiableList(
                new ArrayList<>(dialogues.values())
        );
    }

    public synchronized int getCount() {
        return dialogues.size();
    }

    public synchronized void save(Dialogue dialogue)
            throws SQLException {

        Objects.requireNonNull(
                dialogue,
                "Dialogue cannot be null"
        );

        repository.save(dialogue);
        dialogues.put(dialogue.getId(), dialogue);
    }

    public synchronized boolean delete(String dialogueId)
            throws SQLException {

        boolean deleted = repository.delete(dialogueId);

        if (deleted) {
            dialogues.remove(dialogueId);
        }

        return deleted;
    }

    public synchronized void saveAll() throws SQLException {
        for (Dialogue dialogue : dialogues.values()) {
            repository.save(dialogue);
        }
    }

    public synchronized void unload() {
        dialogues.clear();
    }
}
