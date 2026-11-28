package use_case.load_dashboard;

import entity.Board;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class LoadDashboardInteractorTest {

    private static class FakeDataAccess implements LoadDashboardDataAccessInterface{
        private final List<Board> boards;
        FakeDataAccess(List<Board> boards) {
            this.boards = boards;
        }

        @Override
        public List<Board> getBoardsForUser(String username) {
            return boards;
        }
    }

    private static class FakePresenter implements LoadDashboardOutputBoundary{
        LoadDashboardOutputData successData;
        String error;

        @Override
        public void prepareSuccessView(LoadDashboardOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.error = errorMessage;
        }

    }

    @Test
    void execute_userHasBoards_showsTheirIdsAndTitles() {
        // Arrange: two boards in fake storage
        Board chores = new Board("b1", "Family Chores", "vida");
        Board study = new Board("b2", "Study Group", "vida");
        FakeDataAccess dataAccess = new FakeDataAccess(List.of(chores, study));
        FakePresenter presenter = new FakePresenter();
        LoadDashboardInteractor interactor = new LoadDashboardInteractor(dataAccess, presenter);

        // Act
        interactor.execute(new LoadDashboardInputData("vida"));

        // Assert: success with both boards, no error
        assertEquals(List.of("b1", "b2"), presenter.successData.getBoardIds());
        assertEquals(List.of("Family Chores", "Study Group"), presenter.successData.getBoardTitles());
        assertNull(presenter.error);
    }

    @Test
    void execute_userHasNoBoards_showsEmptyDashboard() {
        // Arrange: fake storage with no boards
        FakeDataAccess dataAccess = new FakeDataAccess(List.of());
        FakePresenter presenter = new FakePresenter();
        LoadDashboardInteractor interactor = new LoadDashboardInteractor(dataAccess, presenter);

        // Act
        interactor.execute(new LoadDashboardInputData("vida"));

        // Assert: success with empty lists, no error
        assertTrue(presenter.successData.getBoardIds().isEmpty());
        assertTrue(presenter.successData.getBoardTitles().isEmpty());
        assertNull(presenter.error);
    }

    @Test
    void execute_boardsCannotLoad_showsError() {
        // Arrange: fake storage returns null, meaning "couldn't load"
        FakeDataAccess dataAccess = new FakeDataAccess(null);
        FakePresenter presenter = new FakePresenter();
        LoadDashboardInteractor interactor = new LoadDashboardInteractor(dataAccess, presenter);

        // Act
        interactor.execute(new LoadDashboardInputData("vida"));

        // Assert: error sent, no success
        assertNull(presenter.successData);
        assertEquals("Couldn't load boards for vida. Try again.", presenter.error);
    }

}

