package entity;

/**
 * A sticker that has been placed on a board at a position.
 */
public class PlacedSticker {
    private final String placementId;
    private final Sticker sticker;
    private final int x;
    private final int y;
    private final String addedBy;

    public PlacedSticker(String placementId, Sticker sticker, int x, int y, String addedBy) {
        this.placementId = placementId;
        this.sticker = sticker;
        this.x = x;
        this.y = y;
        this.addedBy = addedBy;
    }

    public String getPlacementId() {
        return placementId;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getAddedBy() {
        return addedBy;
    }
}