package use_case.add_sticker;

import entity.Board;
import entity.PlacedSticker;
import entity.PlacedStickerFactory;
import entity.Sticker;
import entity.Task;
import entity.TaskStatus;
import use_case.DataAccessException;

/**
 * Use Case 2: add a sticker to a board.
 * A board can hold one sticker per completed task, up to Board.MAX_STICKERS.
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
            final Board board = dataAccess.getBoard(inputData.getBoardId());
            if (board == null) {
                presenter.prepareFailView("This board no longer exists.");
                return;
            }
            if (!board.canEdit(inputData.getUsername())) {
                presenter.prepareFailView("You don't have permission to add stickers to this board.");
                return;
            }
            if (board.isFull()) {
                presenter.prepareFailView("This board is full. Remove a sticker to add a new one.");
                return;
            }
            // Step 2: stickers are unlocked by completed tasks (extension 2a)
            final int completed = countCompletedTasks(board);
            final int placed = board.getStickers().size();
            if (placed >= completed) {
                final int needed = placed - completed + 1;
                String plural = "";
                if (needed != 1) {
                    plural = "s";
                }
                presenter.prepareFailView("Complete " + needed + " more task" + plural
                        + " to unlock another sticker");
                return;
            }

            // Steps 3 to 5: create the sticker at the chosen position and save it
            final Sticker sticker = new Sticker(inputData.getStickerId(), inputData.getTitle(),
                    inputData.getImageUrl());
            final PlacedSticker placedSticker = factory.create(sticker, inputData.getX(), inputData.getY(),
                    inputData.getUsername());
            board.addSticker(placedSticker);
            dataAccess.saveBoard(board);

            presenter.prepareSuccessView(new AddStickerOutputData(
                    board.getId(), placedSticker.getPlacementId(),
                    sticker.getImageUrl(), placedSticker.getX(), placedSticker.getY()));
        } catch (DataAccessException ex) {
            // Extension 5a
            presenter.prepareFailView("Couldn't save the sticker. Try again.");
        }
    }

    /** A task counts once every assignee has finished it (status DONE). */
    private static int countCompletedTasks(Board board) {
        int count = 0;
        for (Task task : board.getTasks()) {
            if (task.getStatus() == TaskStatus.DONE) {
                count++;
            }
        }
        return count;
    }
}