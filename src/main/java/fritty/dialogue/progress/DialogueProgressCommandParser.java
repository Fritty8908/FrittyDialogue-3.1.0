package fritty.dialogue.progress;

import java.util.Locale;

public final class DialogueProgressCommandParser {

    public enum Action {
        RESET,
        ADD_DIALOGUE,
        ADD_BRANCH,
        DELETE_DIALOGUE,
        DELETE_BRANCH,
        INVALID
    }

    public Action parse(String[] args) {
        if (args == null || args.length == 0) {
            return Action.INVALID;
        }

        String operation = args[0].toLowerCase(Locale.ROOT);

        switch (operation) {
            case "reset":
                return args.length == 2
                        ? Action.RESET
                        : Action.INVALID;

            case "add":
                if (args.length == 3) {
                    return Action.ADD_DIALOGUE;
                }

                if (args.length == 4) {
                    return Action.ADD_BRANCH;
                }

                return Action.INVALID;

            case "delete":
                if (args.length == 3) {
                    return Action.DELETE_DIALOGUE;
                }

                if (args.length == 4) {
                    return Action.DELETE_BRANCH;
                }

                return Action.INVALID;

            default:
                return Action.INVALID;
        }
    }

    public String getPlayerName(String[] args) {
        if (args == null || args.length < 2) {
            return null;
        }

        return args[1];
    }

    public String getDialogueId(String[] args) {
        if (args == null || args.length < 3) {
            return null;
        }

        return args[2];
    }

    public String getBranchId(String[] args) {
        if (args == null || args.length < 4) {
            return null;
        }

        return args[3];
    }
}
