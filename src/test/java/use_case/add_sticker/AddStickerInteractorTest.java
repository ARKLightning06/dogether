package use_case.add_sticker;

import data_access.InMemoryStickerDataAccessObject;
import entity.PlacedSticker;
import entity.PlacedStickerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AddStickerInteractorTest {

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

    private InMemoryStickerDataAccessObject data;
    private RecordingPresenter presenter;
    private AddStickerInteractor interactor;

    @BeforeEach
    void setUp() {
        data = new InMemoryStickerDataAccessObject();
        presenter = new RecordingPresenter();
        interactor = new AddStickerInteractor(data, presenter, new PlacedStickerFactory());
    }

    private AddStickerInputData input(int x, int y) {
        return new AddStickerInputData("board1", "marina", "a1", "cat",
                "https://example.com/a1.gif", x, y);
    }

    @Test
    void addsStickerWhenUnlocked() {
        data.setCompletedTasks("board1", 1);

        interactor.execute(input(40, 60));

        assertNull(presenter.error);
        assertEquals(40, presenter.success.getX());
        assertEquals(60, presenter.success.getY());
        assertEquals(1, data.countStickers("board1"));
    }

    @Test
    void failsWhenNoTasksCompleted() {
        interactor.execute(input(0, 0));

        assertEquals("Complete 1 more task to unlock another sticker", presenter.error);
        assertEquals(0, data.countStickers("board1"));
    }

    @Test
    void failsWhenStickersEqualCompletedTasks() {
        data.setCompletedTasks("board1", 1);
        interactor.execute(input(0, 0));

        presenter.error = null;
        interactor.execute(input(10, 10));

        assertEquals("Complete 1 more task to unlock another sticker", presenter.error);
        assertEquals(1, data.countStickers("board1"));
    }

    @Test
    void sameStickerTwiceGetsDifferentPlacementIds() {
        data.setCompletedTasks("board1", 2);
        interactor.execute(input(0, 0));
        final String first = presenter.success.getPlacementId();

        interactor.execute(input(5, 5));

        assertNotEquals(first, presenter.success.getPlacementId());
    }

    @Test
    void saveFailureShowsError() {
        final AddStickerDataAccessInterface broken = new AddStickerDataAccessInterface() {
            @Override
            public int countCompletedTasks(String boardId) {
                return 5;
            }

            @Override
            public int countStickers(String boardId) {
                return 0;
            }

            @Override
            public void saveSticker(String boardId, PlacedSticker sticker) throws AddStickerException {
                throw new AddStickerException("disk full");
            }
        };
        new AddStickerInteractor(broken, presenter, new PlacedStickerFactory()).execute(input(0, 0));

        assertEquals("Couldn't save the sticker. Try again.", presenter.error);
    }
}