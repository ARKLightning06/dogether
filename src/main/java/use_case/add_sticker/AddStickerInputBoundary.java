package use_case.add_sticker;

/**
 * Input boundary for the AddSticker use case. Implemented by AddStickerInteractor.
 */
public interface AddStickerInputBoundary {
    /**
     * Adds a sticker to a board.
     *
     * @param inputData the board, user, sticker, and position
     */
    void execute(AddStickerInputData inputData);
}