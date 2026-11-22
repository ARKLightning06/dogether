package use_case.add_sticker;

/**
 * Output boundary for the AddSticker use case. Implemented by AddStickerPresenter.
 */
public interface AddStickerOutputBoundary {
    /**
     * Called when the sticker was saved to the board.
     *
     * @param outputData the saved sticker's placement id, image, and position
     */
    void prepareSuccessView(AddStickerOutputData outputData);

    /**
     * Called when the sticker could not be added.
     *
     * @param errorMessage what went wrong, shown to the user
     */
    void prepareFailView(String errorMessage);
}