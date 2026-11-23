package use_case.view_completion_count;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Board;
import entity.Task;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ViewCompletionCountInteractor.
 *
 * NOTE: depends on entity.Board and entity.Task from PR #10.
 * Will compile and pass once that PR is merged.
 */
class ViewCompletionCountInteractorTest {

    private static class FakePresenter implements ViewCompletionCountOutputBoundary {
        ViewCompletionCountOutputData successData;
        String failMessage;

        @Override
        public void prepareSuccessView(ViewCompletionCountOutputData out) {
            this.successData = out;
            this.failMessage = null;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
            this.successData = null;
        }
    }

    private static class FakeDataAccess implements ViewCompletionCountDataAccessInterface {
        private final String boardId;
        private final Board board;

        FakeDataAccess(String boardId, Board board) {
            this.boardId = boardId;
            this.board = board;
        }

        @Override
        public Board getBoard(String id) {
            return id.equals(boardId) ? board : null;
        }
    }

    private static class FakeBoard extends Board {
        private final List<Task> tasks;

        FakeBoard(List<Task> tasks) {
            super("board-1", "Test Board", "owner");
            this.tasks = tasks;
        }

        @Override
        public List<Task> getTasks() {
            return tasks;
        }
    }

    private static Task makeTask(String id, String name, String... assignees) {
        return new Task(id, name, "desc", LocalDate.of(2026, 12, 1), "#FFF",
                Arrays.asList(assignees));
    }

    private static final String BOARD_ID = "board-1";
    private static final String USER = "alice";

    private FakePresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = new FakePresenter();
    }

    @Test
    void execute_boardNotFound_callsFailView() {
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, null);
        ViewCompletionCountInteractor interactor = new ViewCompletionCountInteractor(dao, presenter);

        interactor.execute(new ViewCompletionCountInputData(USER, "wrong-board"));

        assertNotNull(presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_noTasksOnBoard_returnsZeroZero() {
        FakeBoard board = new FakeBoard(List.of());
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        ViewCompletionCountInteractor interactor = new ViewCompletionCountInteractor(dao, presenter);

        interactor.execute(new ViewCompletionCountInputData(USER, BOARD_ID));

        assertNotNull(presenter.successData);
        assertEquals(0, presenter.successData.getTotal());
        assertEquals(0, presenter.successData.getCompleted());
    }

    @Test
    void execute_userNotAssignedToAnyTask_returnsZeroZero() {
        Task t1 = makeTask("t1", "Bob's task", "bob");
        FakeBoard board = new FakeBoard(List.of(t1));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        ViewCompletionCountInteractor interactor = new ViewCompletionCountInteractor(dao, presenter);

        interactor.execute(new ViewCompletionCountInputData(USER, BOARD_ID));

        assertNotNull(presenter.successData);
        assertEquals(0, presenter.successData.getTotal());
        assertEquals(0, presenter.successData.getCompleted());
    }

    @Test
    void execute_userAssignedButNoneComplete_returnsCorrectTotal() {
        Task t1 = makeTask("t1", "Task 1", USER);
        Task t2 = makeTask("t2", "Task 2", USER);
        FakeBoard board = new FakeBoard(List.of(t1, t2));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        ViewCompletionCountInteractor interactor = new ViewCompletionCountInteractor(dao, presenter);

        interactor.execute(new ViewCompletionCountInputData(USER, BOARD_ID));

        assertNotNull(presenter.successData);
        assertEquals(2, presenter.successData.getTotal());
        assertEquals(0, presenter.successData.getCompleted());
    }

    @Test
    void execute_userCompletedSomeTasks_returnsCorrectCounts() {
        Task t1 = makeTask("t1", "Task 1", USER);
        Task t2 = makeTask("t2", "Task 2", USER);
        t1.markCompleteBy(USER);
        FakeBoard board = new FakeBoard(List.of(t1, t2));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        ViewCompletionCountInteractor interactor = new ViewCompletionCountInteractor(dao, presenter);

        interactor.execute(new ViewCompletionCountInputData(USER, BOARD_ID));

        assertNotNull(presenter.successData);
        assertEquals(2, presenter.successData.getTotal());
        assertEquals(1, presenter.successData.getCompleted());
    }

    @Test
    void execute_userCompletedAllTasks_returnsEqualCounts() {
        Task t1 = makeTask("t1", "Task 1", USER);
        t1.markCompleteBy(USER);
        FakeBoard board = new FakeBoard(List.of(t1));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        ViewCompletionCountInteractor interactor = new ViewCompletionCountInteractor(dao, presenter);

        interactor.execute(new ViewCompletionCountInputData(USER, BOARD_ID));

        assertNotNull(presenter.successData);
        assertEquals(1, presenter.successData.getTotal());
        assertEquals(1, presenter.successData.getCompleted());
    }

    @Test
    void execute_mixedAssignments_onlyCountsUsersTasks() {
        Task t1 = makeTask("t1", "Alice task", USER);
        Task t2 = makeTask("t2", "Bob task", "bob");
        Task t3 = makeTask("t3", "Shared task", USER, "bob");
        t1.markCompleteBy(USER);
        FakeBoard board = new FakeBoard(List.of(t1, t2, t3));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        ViewCompletionCountInteractor interactor = new ViewCompletionCountInteractor(dao, presenter);

        interactor.execute(new ViewCompletionCountInputData(USER, BOARD_ID));

        assertNotNull(presenter.successData);
        assertEquals(2, presenter.successData.getTotal());
        assertEquals(1, presenter.successData.getCompleted());
    }
}