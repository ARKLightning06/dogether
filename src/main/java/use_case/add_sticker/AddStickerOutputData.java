package use_case.add_sticker;

/**
 * What the board needs to draw the newly saved sticker.
 */
public class AddStickerOutputData {
    private final String boardId;
    private final String placementId;
    private final String imageUrl;
    private final int x;
    private final int y;

    public AddStickerOutputData(String boardId, String placementId, String imageUrl, int x, int y) {
        this.boardId = boardId;
        this.placementId = placementId;
        this.imageUrl = imageUrl;
        this.x = x;
        this.y = y;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getPlacementId() {
        return placementId;
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