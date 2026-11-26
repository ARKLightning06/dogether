package interface_adapter.add_sticker;

import use_case.add_sticker.AddStickerInputBoundary;
import use_case.add_sticker.AddStickerInputData;

/**
 * Turns "save this sticker here" from the board view into input data for the use case.
 */
public class AddStickerController {
    private final AddStickerInputBoundary interactor;

    public AddStickerController(AddStickerInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Adds a sticker to a board.
     *
     * @param boardId the board
     * @param username who is adding it
     * @param stickerId the sticker's id from search
     * @param title the sticker's title (may be empty)
     * @param imageUrl the sticker's image
     * @param x where it was dropped, from the left of the board
     * @param y where it was dropped, from the top of the board
     */
    public void execute(String boardId, String username, String stickerId, String title,
                        String imageUrl, int x, int y) {
        interactor.execute(new AddStickerInputData(boardId, username, stickerId, title, imageUrl, x, y));
    }
}
