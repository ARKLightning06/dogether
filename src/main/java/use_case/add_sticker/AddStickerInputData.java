package use_case.add_sticker;

/**
 * What the board member chose: which sticker, where, and on which board.
 */
public class AddStickerInputData {
    private final String boardId;
    private final String username;
    private final String stickerId;
    private final String title;
    private final String imageUrl;
    private final int x;
    private final int y;

    public AddStickerInputData(String boardId, String username, String stickerId,
                               String title, String imageUrl, int x, int y) {
        this.boardId = boardId;
        this.username = username;
        this.stickerId = stickerId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.x = x;
        this.y = y;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getUsername() {
        return username;
    }

    public String getStickerId() {
        return stickerId;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}