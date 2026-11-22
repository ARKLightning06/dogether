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

// TODO: switch to getBoard(boardId) / saveBoard(board) once Board (#15) is merged,
//  so the interactor can use board.canEdit, board.isFull, and board.addSticker.