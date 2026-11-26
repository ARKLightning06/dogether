package use_case.add_sticker;

import entity.Board;

/**
 * What the add-sticker use case needs from storage.
 * Methods may throw use_case.DataAccessException if storage can't be reached.
 */
public interface AddStickerDataAccessInterface {
    /**
     * Loads a board.
     *
     * @param boardId the board's id
     * @return the board, or null if it doesn't exist
     */
    Board getBoard(String boardId);

    /**
     * Saves a board, including its stickers.
     *
     * @param board the board to save
     */
    void saveBoard(Board board);
}