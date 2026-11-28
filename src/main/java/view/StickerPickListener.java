package view;

/**
 * Told when the user clicks a sticker in the search results.
 */
public interface StickerPickListener {
    /**
     * Called when a sticker is picked.
     *
     * @param stickerId the sticker's id
     * @param imageUrl the sticker's image
     */
    void stickerPicked(String stickerId, String imageUrl);
}
