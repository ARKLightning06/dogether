package use_case.filter_tasks_by_status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.Board;
import entity.Task;
import entity.TaskStatus;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for FilterTasksInteractor.
 *
 * NOTE: depends on entity.Board and entity.Task from PR #10.
 * Will compile and pass once that PR is merged.
 */
class FilterTasksInteractorTest {

    private static class FakePresenter implements FilterTasksOutputBoundary {
        FilterTasksOutputData successData;
        String failMessage;

        @Override
        public void prepareSuccessView(FilterTasksOutputData out) {
            this.successData = out;
            this.failMessage = null;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
            this.successData = null;
        }
    }

    private static class FakeDataAccess implements FilterTasksDataAccessInterface {
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
        private final List<String> members;
        private final List<Task> tasks;

        FakeBoard(List<String> members, List<Task> tasks) {
            super("board-1", "Test Board", "owner");
            this.members = members;
            this.tasks = tasks;
        }

        @Override
        public boolean hasMember(String username) {
            return members.contains(username);
        }

        @Override
        public List<Task> getTasks() {
            return tasks;
        }
    }

    private static Task makeTask(String id, String name, TaskStatus status, String... assignees) {
        Task task = new Task(id, name, "desc", LocalDate.of(2026, 12, 1), "#FFF",
                Arrays.asList(assignees));
        if (status == TaskStatus.IN_PROGRESS) {
            task.update(name, "desc", null, "#FFF", Arrays.asList(assignees), TaskStatus.IN_PROGRESS);
        } else if (status == TaskStatus.DONE && assignees.length > 0) {
            for (String a : assignees) {
                task.markCompleteBy(a);
            }
        }
        return task;
    }

    private static final String BOARD_ID = "board-1";
    private static final String MEMBER = "alice";
    private static final String NON_MEMBER = "eve";

    private FakePresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = new FakePresenter();
    }

    @Test
    void execute_boardNotFound_callsFailView() {
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, null);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData(MEMBER, "wrong-board", TaskStatus.TO_DO, null));

        assertNotNull(presenter.failMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_userNotMember_callsFailView() {
        FakeBoard board = new FakeBoard(List.of(MEMBER), List.of());
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData(NON_MEMBER, BOARD_ID, TaskStatus.TO_DO, null));

        assertNotNull(presenter.failMessage);
    }

    @Test
    void execute_userIsMember_doesNotCallFailView() {
        FakeBoard board = new FakeBoard(List.of(MEMBER), List.of());
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData(MEMBER, BOARD_ID, TaskStatus.TO_DO, null));

        assertNull(presenter.failMessage);
    }

    @Test
    void execute_filterByToDoStatus_returnsOnlyToDoTasks() {
        Task todo = makeTask("t1", "Todo task", TaskStatus.TO_DO, MEMBER);
        Task inProgress = makeTask("t2", "In-progress task", TaskStatus.IN_PROGRESS, MEMBER);
        FakeBoard board = new FakeBoard(List.of(MEMBER), List.of(todo, inProgress));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData(MEMBER, BOARD_ID, TaskStatus.TO_DO, null));

        assertNotNull(presenter.successData);
        assertEquals(List.of("t1"), presenter.successData.getTaskIds());
    }

    @Test
    void execute_noTasksMatchStatus_returnsEmptyLists() {
        Task todo = makeTask("t1", "Todo task", TaskStatus.TO_DO, MEMBER);
        FakeBoard board = new FakeBoard(List.of(MEMBER), List.of(todo));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData(MEMBER, BOARD_ID, TaskStatus.DONE, null));

        assertNotNull(presenter.successData);
        assertTrue(presenter.successData.getTaskIds().isEmpty());
    }

    @Test
    void execute_filterByMember_returnsOnlyAssignedTasks() {
        Task t1 = makeTask("t1", "Alice task", TaskStatus.TO_DO, "alice");
        Task t2 = makeTask("t2", "Bob task", TaskStatus.TO_DO, "bob");
        FakeBoard board = new FakeBoard(List.of("alice", "bob"), List.of(t1, t2));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData("alice", BOARD_ID, TaskStatus.TO_DO, List.of("alice")));

        assertNotNull(presenter.successData);
        assertEquals(List.of("t1"), presenter.successData.getTaskIds());
    }

    @Test
    void execute_nullMemberFilter_treatedAsNoFilter() {
        Task t1 = makeTask("t1", "Task 1", TaskStatus.TO_DO, "alice");
        Task t2 = makeTask("t2", "Task 2", TaskStatus.TO_DO, "bob");
        FakeBoard board = new FakeBoard(List.of("alice", "bob"), List.of(t1, t2));
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData("alice", BOARD_ID, TaskStatus.TO_DO, null));

        assertNotNull(presenter.successData);
        assertEquals(2, presenter.successData.getTaskIds().size());
    }

    @Test
    void execute_emptyBoard_returnsEmptyLists() {
        FakeBoard board = new FakeBoard(List.of(MEMBER), List.of());
        FakeDataAccess dao = new FakeDataAccess(BOARD_ID, board);
        FilterTasksInteractor interactor = new FilterTasksInteractor(presenter, dao);

        interactor.execute(new FilterTasksInputData(MEMBER, BOARD_ID, TaskStatus.TO_DO, null));

        assertNotNull(presenter.successData);
        assertTrue(presenter.successData.getTaskIds().isEmpty());
    }
}