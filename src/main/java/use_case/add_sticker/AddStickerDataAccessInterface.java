package use_case.add_sticker;

import entity.PlacedSticker;

/**
 * What the add-sticker use case needs from storage.
 */
public interface AddStickerDataAccessInterface {
    int countCompletedTasks(String boardId) throws AddStickerException;

    int countStickers(String boardId) throws AddStickerException;

    void saveSticker(String boardId, PlacedSticker sticker) throws AddStickerException;
}