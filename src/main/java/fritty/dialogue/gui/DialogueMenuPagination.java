package fritty.dialogue.gui;

public final class DialogueMenuPagination {

    private DialogueMenuPagination() {
    }

    public static int getPageCount(int itemCount, int itemsPerPage) {
        if (itemsPerPage <= 0) {
            throw new IllegalArgumentException(
                    "Items per page must be greater than zero"
            );
        }

        if (itemCount <= 0) {
            return 1;
        }

        return (itemCount + itemsPerPage - 1) / itemsPerPage;
    }

    public static int normalizePage(
            int requestedPage,
            int itemCount,
            int itemsPerPage
    ) {
        int pageCount = getPageCount(itemCount, itemsPerPage);

        return Math.max(
                0,
                Math.min(requestedPage, pageCount - 1)
        );
    }

    public static int getStartIndex(int page, int itemsPerPage) {
        if (page < 0 || itemsPerPage <= 0) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters"
            );
        }

        return page * itemsPerPage;
    }

    public static int getEndIndex(
            int page,
            int itemCount,
            int itemsPerPage
    ) {
        int start = getStartIndex(page, itemsPerPage);

        return Math.min(
                start + itemsPerPage,
                Math.max(0, itemCount)
        );
    }

    public static boolean hasPreviousPage(int page) {
        return page > 0;
    }

    public static boolean hasNextPage(
            int page,
            int itemCount,
            int itemsPerPage
    ) {
        return page + 1 < getPageCount(itemCount, itemsPerPage);
    }

    public static int getPreviousPage(int page) {
        return Math.max(0, page - 1);
    }

    public static int getNextPage(
            int page,
            int itemCount,
            int itemsPerPage
    ) {
        int lastPage = getPageCount(itemCount, itemsPerPage) - 1;

        return Math.min(page + 1, lastPage);
    }
}
