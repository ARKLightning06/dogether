package use_case.add_sticker;

import entity.PlacedSticker;
import entity.PlacedStickerFactory;
import entity.Sticker;

/**
 * Use Case 2: add a sticker to a board.
 * A board can hold one sticker per completed task.
 */
public class AddStickerInteractor implements AddStickerInputBoundary {
    private final AddStickerDataAccessInterface dataAccess;
    private final AddStickerOutputBoundary presenter;
    private final PlacedStickerFactory factory;

    public AddStickerInteractor(AddStickerDataAccessInterface dataAccess,
                                AddStickerOutputBoundary presenter,
                                PlacedStickerFactory factory) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.factory = factory;
    }

    @Override
    public void execute(AddStickerInputData inputData) {
        try {
            // Step 2: stickers are unlocked by completed tasks (extension 2a)
            int completed = dataAccess.countCompletedTasks(inputData.getBoardId());
            int placed = dataAccess.countStickers(inputData.getBoardId());
            if (placed >= completed) {
                int needed = placed - completed + 1;
                String plural = "";
                if (needed != 1) {
                    plural = "s";
                }
                presenter.prepareFailView("Complete " + needed + " more task" + plural
                        + " to unlock another sticker");
                return;
            }

            // Steps 3 to 5: create the sticker at the chosen position and save it
            Sticker sticker = new Sticker(inputData.getStickerId(), inputData.getTitle(),
                    inputData.getImageUrl());
            PlacedSticker placedSticker = factory.create(sticker, inputData.getX(), inputData.getY(),
                    inputData.getUsername());
            dataAccess.saveSticker(inputData.getBoardId(), placedSticker);

            presenter.prepareSuccessView(new AddStickerOutputData(
                    inputData.getBoardId(), placedSticker.getPlacementId(),
                    sticker.getImageUrl(), placedSticker.getX(), placedSticker.getY()));
        }
        catch (AddStickerException ex) {
            // Extension 5a
            presenter.prepareFailView("Couldn't save the sticker. Try again.");
        }
    }
}