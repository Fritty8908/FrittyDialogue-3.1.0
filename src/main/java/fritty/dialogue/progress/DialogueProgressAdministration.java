package fritty.dialogue.progress;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public final class DialogueProgressAdministration {

    private final ProgressManager progressManager;

    public DialogueProgressAdministration(
            ProgressManager progressManager
    ) {
        this.progressManager = Objects.requireNonNull(progressManager);
    }

    public void reset(UUID playerId) throws SQLException {
        progressManager.resetPlayer(Objects.requireNonNull(playerId));
    }

    public void completeDialogue(
            UUID playerId,
            String dialogueId
    ) throws SQLException {
        progressManager.completeDialogue(
                Objects.requireNonNull(playerId),
                requireId(dialogueId)
        );
    }

    public void completeBranch(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {
        progressManager.completeBranch(
                Objects.requireNonNull(playerId),
                requireId(dialogueId),
                requireId(branchId)
        );
    }

    public void deleteDialogue(
            UUID playerId,
            String dialogueId
    ) throws SQLException {
        progressManager.deleteDialogueProgress(
                Objects.requireNonNull(playerId),
                requireId(dialogueId)
        );
    }

    public void deleteBranch(
            UUID playerId,
            String dialogueId,
            String branchId
    ) throws SQLException {
        progressManager.deleteBranchProgress(
                Objects.requireNonNull(playerId),
                requireId(dialogueId),
                requireId(branchId)
        );
    }

    public void deleteAnswer(
            UUID playerId,
            String dialogueId,
            String branchId,
            String answerId
    ) throws SQLException {
        progressManager.deleteAnswerProgress(
                Objects.requireNonNull(playerId),
                requireId(dialogueId),
                requireId(branchId),
                requireId(answerId)
        );
    }

    public OfflinePlayer findPlayer(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        return Bukkit.getOfflinePlayer(name);
    }

    private String requireId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ID cannot be blank");
        }

        return value.trim();
    }
}
