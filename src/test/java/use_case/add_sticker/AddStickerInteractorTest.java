package use_case.add_sticker;

import data_access.InMemoryBoardDataAccessObject;
import entity.Board;
import entity.Permission;
import entity.PlacedSticker;
import entity.PlacedStickerFactory;
import entity.Sticker;
import entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.DataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddStickerInteractorTest {
    private static final String OWNER = "marina";

    /** Records what the interactor sent to the presenter. */
    private static class RecordingPresenter implements AddStickerOutputBoundary {
        private AddStickerOutputData success;
        private String error;

        @Override
        public void prepareSuccessView(AddStickerOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            error = errorMessage;
        }
    }

    private InMemoryBoardDataAccessObject data;
    private RecordingPresenter presenter;
    private AddStickerInteractor interactor;
    private Board board;

    @BeforeEach
    void setUp() {
        data = new InMemoryBoardDataAccessObject();
        presenter = new RecordingPresenter();
        interactor = new AddStickerInteractor(data, presenter, new PlacedStickerFactory());
        board = new Board("board1", "Test board", OWNER);
        data.saveBoard(board);
    }

    /** Adds tasks to the board; the first `completed` of them are finished. */
    private void addTasks(int total, int completed) {
        for (int i = 0; i < total; i++) {
            final Task task = new Task("t" + i, "Task " + i, "", null, "#FFFFFF", List.of(OWNER));
            if (i < completed) {
                task.markCompleteBy(OWNER);
            }
            board.addTask(task);
        }
    }

    private AddStickerInputData input(String username, int x, int y) {
        return new AddStickerInputData("board1", username, "a1", "cat",
                "https://example.com/a1.gif", x, y);
    }

    @Test
    void addsStickerWhenUnlocked() {
        addTasks(2, 1);

        interactor.execute(input(OWNER, 40, 60));

        assertNull(presenter.error);
        assertEquals(40, presenter.success.getX());
        assertEquals(60, presenter.success.getY());
        assertEquals(1, data.getBoard("board1").getStickers().size());
    }

    @Test
    void failsWhenNoTasksCompleted() {
        addTasks(2, 0);

        interactor.execute(input(OWNER, 0, 0));

        assertEquals("Complete 1 more task to unlock another sticker", presenter.error);
        assertEquals(0, board.getStickers().size());
    }

    @Test
    void failsWhenStickersEqualCompletedTasks() {
        addTasks(1, 1);
        interactor.execute(input(OWNER, 0, 0));

        presenter.error = null;
        interactor.execute(input(OWNER, 10, 10));

        assertEquals("Complete 1 more task to unlock another sticker", presenter.error);
        assertEquals(1, board.getStickers().size());
    }

    @Test
    void sameStickerTwiceGetsDifferentPlacementIds() {
        addTasks(2, 2);
        interactor.execute(input(OWNER, 0, 0));
        final String first = presenter.success.getPlacementId();

        interactor.execute(input(OWNER, 5, 5));

        assertNotEquals(first, presenter.success.getPlacementId());
    }

    @Test
    void boardNotFoundFails() {
        new AddStickerInteractor(data, presenter, new PlacedStickerFactory()).execute(
                new AddStickerInputData("missing", OWNER, "a1", "cat", "https://example.com/a1.gif", 0, 0));

        assertEquals("This board no longer exists.", presenter.error);
    }

    @Test
    void nonMemberCannotAddSticker() {
        addTasks(1, 1);

        interactor.execute(input("stranger", 0, 0));

        assertEquals("You don't have permission to add stickers to this board.", presenter.error);
        assertEquals(0, board.getStickers().size());
    }

    @Test
    void viewOnlyMemberCannotAddSticker() {
        addTasks(1, 1);
        board.addMember("viewer", Permission.VIEW);

        interactor.execute(input("viewer", 0, 0));

        assertEquals("You don't have permission to add stickers to this board.", presenter.error);
    }

    @Test
    void fullBoardCannotTakeMoreStickers() {
        addTasks(Board.MAX_STICKERS + 1, Board.MAX_STICKERS + 1);
        for (int i = 0; i < Board.MAX_STICKERS; i++) {
            board.addSticker(new PlacedSticker("p" + i, new Sticker("s", "", "u"), 0, 0, OWNER));
        }

        interactor.execute(input(OWNER, 0, 0));

        assertEquals("This board is full. Remove a sticker to add a new one.", presenter.error);
        assertEquals(Board.MAX_STICKERS, board.getStickers().size());
    }

    @Test
    void saveFailureShowsError() {
        addTasks(1, 1);
        final AddStickerDataAccessInterface broken = new AddStickerDataAccessInterface() {
            @Override
            public Board getBoard(String boardId) {
                return board;
            }

            @Override
            public void saveBoard(Board savedBoard) {
                throw new DataAccessException("disk full");
            }
        };
        new AddStickerInteractor(broken, presenter, new PlacedStickerFactory()).execute(input(OWNER, 0, 0));

        assertEquals("Couldn't save the sticker. Try again.", presenter.error);
    }
}
