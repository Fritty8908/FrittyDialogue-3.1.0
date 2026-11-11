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

    private final Map<UUID, Object> playerLocks =
            new ConcurrentHashMap<>();

    public ProgressManager(ProgressRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Progress repository cannot be null"
            );
        }

        this.repository = repository;
    }

    public PlayerProgress get(UUID playerId)
            throws SQLException {

        requirePlayerId(playerId);

        synchronized (getPlayerLock(playerId)) {
            PlayerProgress cached = cache.get(playerId);

            if (cached != null) {
                return cached;
            }

            PlayerProgress loaded =
                    repository.load(playerId);

            cache.put(playerId, loaded);

            return loaded;
        }
    }

    public boolean hasCompletedDialogue(
            UUID playerId,
            String dialogueId
    ) throws SQLException {

        return get(playerId)
                .hasCompletedDialogue(dialogueId);
    }

    public boolean hasCompletedBranch(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {

        return get(playerId)
                .hasCompletedBranch(
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

        return get(playerId)
                .hasSelectedAnswer(
                        dialogueId,
                        branchId,
                        answerId
                );
    }

    public void completeDialogue(
            UUID playerId,
            String dialogueId
    ) throws SQLException {

        synchronized (getPlayerLock(playerId)) {
            repository.completeDialogue(
                    playerId,
                    dialogueId
            );

            get(playerId).addCompletedDialogue(dialogueId);
        }
    }

    public void completeBranch(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {

        synchronized (getPlayerLock(playerId)) {
            repository.completeBranch(
                    playerId,
                    dialogueId,
                    branchId
            );

            get(playerId).addCompletedBranch(
                    dialogueId,
                    branchId
            );
        }
    }

    public void selectAnswer(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {

        synchronized (getPlayerLock(playerId)) {
            repository.selectAnswer(
                    playerId,
                    dialogueId,
                    branchId,
                    answerId
            );

            get(playerId).addSelectedAnswer(
                    dialogueId,
                    branchId,
                    answerId
            );
        }
    }

    public void resetPlayer(UUID playerId)
            throws SQLException {

        requirePlayerId(playerId);

        synchronized (getPlayerLock(playerId)) {
            repository.resetPlayer(playerId);
            cache.remove(playerId);
        }
    }

    public void deleteDialogueProgress(
            UUID playerId,
            String dialogueId
    ) throws SQLException {

        synchronized (getPlayerLock(playerId)) {
            repository.deleteDialogueProgress(
                    playerId,
                    dialogueId
            );

            PlayerProgress progress = get(playerId);

            progress.removeCompletedDialogue(dialogueId);
            progress.removeDialogueProgress(dialogueId);
        }
    }

    public void deleteBranchProgress(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {

        synchronized (getPlayerLock(playerId)) {
            repository.deleteBranchProgress(
                    playerId,
                    dialogueId,
                    branchId
            );

            PlayerProgress progress = get(playerId);

            progress.removeCompletedBranch(
                    dialogueId,
                    branchId
            );

            progress.removeBranchAnswers(
                    dialogueId,
                    branchId
            );
        }
    }

    public void deleteAnswerProgress(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {

        synchronized (getPlayerLock(playerId)) {
            repository.deleteAnswerProgress(
                    playerId,
                    dialogueId,
                    branchId,
                    answerId
            );

            get(playerId).removeSelectedAnswer(
                    dialogueId,
                    branchId,
                    answerId
            );
        }
    }

    public void unloadPlayer(UUID playerId) {
        if (playerId == null) {
            return;
        }

        synchronized (getPlayerLock(playerId)) {
            cache.remove(playerId);
        }
    }

    public void clearCache() {
        cache.clear();
    }

    public int getCachedPlayerCount() {
        return cache.size();
    }

    private Object getPlayerLock(UUID playerId) {
        requirePlayerId(playerId);

        return playerLocks.computeIfAbsent(
                playerId,
                ignored -> new Object()
        );
    }

    private void requirePlayerId(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException(
                    "Player UUID cannot be null"
            );
        }
    }
}
