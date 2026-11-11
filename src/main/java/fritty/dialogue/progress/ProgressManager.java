package fritty.dialogue.progress;

import fritty.dialogue.storage.ProgressRepository;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProgressManager {

    private final ProgressRepository repository;

    private final Map<UUID, PlayerProgress> cache =
            new ConcurrentHashMap<>();

    public ProgressManager(ProgressRepository repository) {
        this.repository = repository;
    }

    public PlayerProgress get(UUID playerId) throws SQLException {
        return cache.computeIfAbsent(playerId, id -> {
            try {
                return repository.load(id);
            } catch (SQLException exception) {
                throw new ProgressLoadException(exception);
            }
        });
    }

    public void unload(UUID playerId) {
        cache.remove(playerId);
    }

    public boolean hasCompletedDialogue(
            UUID playerId,
            String dialogueId
    ) throws SQLException {
        return get(playerId).hasCompletedDialogue(dialogueId);
    }

    public boolean hasCompletedBranch(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {
        return get(playerId).hasCompletedBranch(
                dialogueId,
                branchId
        );
    }

    public boolean hasSelectedAnswer(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {
        return get(playerId).hasSelectedAnswer(
                dialogueId,
                branchId,
                answerId
        );
    }

    public void completeDialogue(
            UUID playerId,
            String dialogueId
    ) throws SQLException {
        get(playerId).completeDialogue(dialogueId);
        repository.completeDialogue(playerId, dialogueId);
    }

    public void completeBranch(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {
        get(playerId).completeBranch(dialogueId, branchId);
        repository.completeBranch(playerId, dialogueId, branchId);
    }

    public void selectAnswer(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {
        get(playerId).selectAnswer(dialogueId, branchId, answerId);

        repository.selectAnswer(
                playerId,
                dialogueId,
                branchId,
                answerId
        );
    }

    public void reset(UUID playerId) throws SQLException {
        repository.resetPlayer(playerId);

        PlayerProgress progress = cache.get(playerId);

        if (progress != null) {
            progress.reset();
        }
    }

    public void deleteDialogueProgress(
            UUID playerId,
            String dialogueId
    ) throws SQLException {
        get(playerId).removeDialogue(dialogueId);

        repository.deleteDialogueProgress(playerId, dialogueId);
    }

    public void deleteBranchProgress(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {
        get(playerId).removeBranch(dialogueId, branchId);

        repository.deleteBranchProgress(
                playerId,
                dialogueId,
                branchId
        );
    }

    public void deleteAnswerProgress(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {
        get(playerId).removeAnswer(dialogueId, branchId, answerId);

        repository.deleteAnswerProgress(
                playerId,
                dialogueId,
                branchId,
                answerId
        );
    }

    public void markDialogueCompleted(
            UUID playerId,
            String dialogueId
    ) throws SQLException {
        completeDialogue(playerId, dialogueId);
    }

    public void markBranchCompleted(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {
        completeBranch(playerId, dialogueId, branchId);
    }

    public void markAnswerSelected(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {
        selectAnswer(playerId, dialogueId, branchId, answerId);
    }

    public void unloadAll() {
        cache.clear();
    }

    private static final class ProgressLoadException
            extends RuntimeException {

        private ProgressLoadException(SQLException cause) {
            super(cause);
        }
    }
}
