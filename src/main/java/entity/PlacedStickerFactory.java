package entity;

import java.util.UUID;

/**
 * Creates PlacedSticker objects with a unique placement id, so the same sticker
 * can be placed on a board more than once and each copy removed separately.
 */
public class PlacedStickerFactory {
    public PlacedSticker create(Sticker sticker, int x, int y, String addedBy) {
        return new PlacedSticker(UUID.randomUUID().toString(), sticker, x, y, addedBy);
    }
}