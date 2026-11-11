package fritty.dialogue.session;

public enum DialogueInputAction {

    PREVIOUS,
    NEXT,
    CONFIRM,
    CANCEL,
    UNKNOWN;

    public static DialogueInputAction fromKey(char key) {
        switch (Character.toLowerCase(key)) {
            case 'w':
                return PREVIOUS;

            case 's':
                return NEXT;

            case 'f':
                return CONFIRM;

            case 'q':
            case 'e':
                return CANCEL;

            default:
                return UNKNOWN;
        }
    }
}
