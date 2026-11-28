package use_case.edit_task;

import entity.Board;
import entity.Permission;
import entity.Task;
import entity.TaskStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import use_case.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Edit Task interactor.
 */
class EditTaskInteractorTest {
    private Board board;
    private Task original;
    private RecordingDAO dao;
    private RecordingPresenter presenter;
    private EditTaskInteractor interactor;

    private static class RecordingDAO implements EditTaskDataAccessInterface {
        private Board board;
        private int saves;
        private boolean loadFails;
        private boolean saveFails;

        @Override
        public Board getBoard(String boardId) {
            if (loadFails) throw new DataAccessException("load failed");
            return board.getId().equals(boardId) ? board : null;
        }

        @Override
        public void saveBoard(Board savedBoard) {
            if (saveFails) throw new DataAccessException("save failed");
            board = savedBoard;
            saves++;
        }
    }

    private static class RecordingPresenter implements EditTaskOutputBoundary {
        private EditTaskOutputData success;
        private String error;

        @Override
        public void prepareSuccessView(EditTaskOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            error = errorMessage;
        }
    }

    @BeforeEach
    void setUp() {
        board = new Board("board1", "Test board", "alice");
        board.addMember("bob", Permission.EDIT);
        board.addMember("viewer", Permission.VIEW);
        original = new Task("task1", "Original", "", LocalDate.of(2026, 12, 1),
                "#FFFFFF", List.of("alice"));
        board.addTask(original);
        dao = new RecordingDAO();
        dao.board = board;
        presenter = new RecordingPresenter();
        interactor = new EditTaskInteractor(dao, presenter);
    }

    private void execute(String user, String boardId, String taskId, String name,
                         LocalDate deadline, String colour, List<String> assignees,
                         TaskStatus status) {
        interactor.execute(new EditTaskInputData(user, boardId, taskId, name, "Updated description", deadline, colour, assignees, status));
    }

    private void validInput() {
        execute("alice", "board1", "task1", "Updated", null, "#D4537E",
                List.of("bob"), TaskStatus.IN_PROGRESS);
    }

    private void assertFailure(String error) {
        assertEquals(error, presenter.error);
        assertNull(presenter.success);
        assertEquals(0, dao.saves);
    }

    @Test
    void successfulExecution() {
        validInput();
        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saves);
        assertEquals("Updated", presenter.success.getName());
        Task saved = dao.board.getTask(presenter.success.getTaskId());
        assertNotNull(saved);
        assertEquals("Updated", saved.getName());
        assertEquals("Updated description", saved.getDescription());
        assertEquals("#D4537E", saved.getColour());
        assertEquals(List.of("bob"), saved.getAssignees());
        assertNull(saved.getDeadline());
        assertEquals(TaskStatus.IN_PROGRESS, saved.getStatus());
        assertEquals(1, dao.board.getTasks().size());
        assertEquals("task1", saved.getId());
        assertEquals("Original", original.getName());
    }

    @Test
    void boardNotFound() {
        execute("alice", "missing", "task1", "Updated", null, "#FFFFFF", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertFailure("Board not found.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void nonMemberRejected() {
        execute("stranger", "board1", "task1", "Updated", null, "#FFFFFF", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertFailure("You do not have permission to edit tasks on this board.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void viewOnlyRejected() {
        execute("viewer", "board1", "task1", "Updated", null, "#FFFFFF", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertFailure("You do not have permission to edit tasks on this board.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void nonMemberAssigneeRejected() {
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF", List.of("stranger"), TaskStatus.IN_PROGRESS);
        assertFailure("All task assignees must be board members.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void nullAssigneesRejected() {
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF", null, TaskStatus.IN_PROGRESS);
        assertFailure("Task assignees cannot be null.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void duplicateAssigneesRejected() {
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF", List.of("bob", "bob"), TaskStatus.IN_PROGRESS);
        assertFailure("Task cannot contain duplicate assignees.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void blankNameRejected() {
        execute("alice", "board1", "task1", " ", null, "#FFFFFF", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertFailure("Task name cannot be empty.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void taskNotFound() {
        execute("alice", "board1", "missing", "Updated", null, "#FFFFFF", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertFailure("Task not found.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void nullStatusRejected() {
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF", List.of("bob"), null);
        assertFailure("Task status cannot be null.");
        assertEquals("Original", original.getName());
        assertEquals(1, board.getTasks().size());
    }

    @Test
    void blankColourRejected() {
        execute("alice", "board1", "task1", "Updated", null, "", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertNull(presenter.success);
        assertNotNull(presenter.error);
        assertEquals(0, dao.saves);
        assertEquals("Original", original.getName());
    }

    @Test
    void loadFailureShowsError() {
        dao.loadFails = true;
        validInput();
        assertFailure("Unable to load the board.");
    }

    @Test
    void saveFailureShowsError() {
        original.markCompleteBy("alice");
        dao.saveFails = true;
        validInput();
        assertFailure("Unable to save the updated task.");
        Task stored = dao.getBoard("board1").getTask("task1");
        assertEquals("Original", stored.getName());
        assertEquals("", stored.getDescription());
        assertEquals(LocalDate.of(2026, 12, 1), stored.getDeadline());
        assertEquals("#FFFFFF", stored.getColour());
        assertEquals(List.of("alice"), stored.getAssignees());
        assertEquals(TaskStatus.DONE, stored.getStatus());
        assertTrue(stored.isCompletedBy("alice"));
    }

    @Test
    void successfulEditPreservesOtherTasksAndCompletionRecords() {
        original.markCompleteBy("alice");
        Task other = new Task("task2", "Other", "", null,
                "#FFFFFF", List.of("bob"));
        other.markCompleteBy("bob");
        board.addTask(other);
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF",
                List.of("alice", "bob"), TaskStatus.IN_PROGRESS);
        assertNull(presenter.error);
        Board stored = dao.getBoard("board1");
        assertEquals(board.getMembers(), stored.getMembers());
        assertEquals(board.getTitle(), stored.getTitle());
        assertEquals(board.getOwnerUsername(), stored.getOwnerUsername());
        assertEquals(2, stored.getTasks().size());
        Task savedOther = stored.getTask("task2");
        assertEquals(other.getName(), savedOther.getName());
        assertEquals(other.getDescription(), savedOther.getDescription());
        assertEquals(other.getDeadline(), savedOther.getDeadline());
        assertEquals(other.getColour(), savedOther.getColour());
        assertEquals(other.getAssignees(), savedOther.getAssignees());
        assertEquals(other.getStatus(), savedOther.getStatus());
        assertEquals(other.getCompletedBy(), savedOther.getCompletedBy());
        assertTrue(stored.getTask("task1").isCompletedBy("alice"));
        assertEquals(List.of("alice"), original.getAssignees());
        assertEquals(TaskStatus.DONE, original.getStatus());
    }

    @Test
    void acceptsDeadlineAndEmptyAssignees() {
        LocalDate deadline = LocalDate.of(2026, 12, 5);
        execute("bob", "board1", "task1", "Updated", deadline, "#FFFFFF",
                List.of(), TaskStatus.IN_PROGRESS);
        assertNull(presenter.error);
        Task saved = dao.board.getTask(presenter.success.getTaskId());
        assertEquals(deadline, saved.getDeadline());
        assertTrue(saved.getAssignees().isEmpty());
    }

    @Test
    void malformedHexColourRejected() {
        execute("alice", "board1", "task1", "Updated", null, "#GGGGGG",
                List.of("alice"), TaskStatus.IN_PROGRESS);
        assertFailure("Enter a valid colour.");
        assertEquals("#FFFFFF", dao.board.getTask("task1").getColour());
    }

    @Test
    void nullDescriptionRejected() {
        interactor.execute(new EditTaskInputData("alice", "board1", "task1",
                "Updated", null, null, "#FFFFFF", List.of("alice"), TaskStatus.IN_PROGRESS));
        assertFailure("Task description cannot be null.");
        assertEquals("", dao.board.getTask("task1").getDescription());
    }

    @Test
    void nullAssigneeElementRejected() {
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF",
                Arrays.asList("alice", null), TaskStatus.IN_PROGRESS);
        assertFailure("All task assignees must be board members.");
        assertEquals(List.of("alice"), dao.board.getTask("task1").getAssignees());
    }

    @Test
    void doneStatusRequiresAllAssigneesToComplete() {
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF",
                List.of("alice"), TaskStatus.DONE);
        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saves);
        assertEquals(TaskStatus.IN_PROGRESS, dao.board.getTask("task1").getStatus());
        assertFalse(dao.board.getTask("task1").isFullyComplete());
    }

    @Test
    void doneStatusAcceptedWhenAllAssigneesHaveCompleted() {
        original.markCompleteBy("alice");
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF",
                List.of("alice"), TaskStatus.DONE);
        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saves);
        assertEquals(TaskStatus.DONE, dao.board.getTask("task1").getStatus());
        assertTrue(dao.board.getTask("task1").isFullyComplete());
    }

    @Test
    void completedTaskRemainsDoneWhenToDoRequested() {
        original.markCompleteBy("alice");
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF",
                List.of("alice"), TaskStatus.TO_DO);
        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saves);
        Task saved = dao.board.getTask("task1");
        assertEquals(TaskStatus.DONE, saved.getStatus());
        assertTrue(saved.isCompletedBy("alice"));
    }

    @Test
    void removesExistingDeadline() {
        assertNotNull(original.getDeadline());
        execute("alice", "board1", "task1", "Original", null, "#FFFFFF",
                List.of("alice"), TaskStatus.TO_DO);
        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saves);
        assertNull(dao.board.getTask("task1").getDeadline());
    }

    @Test
    void removesCompletionRecordsOfRemovedAssignees() {
        original.update("Original", "", null, "#FFFFFF",
                List.of("alice", "bob"), TaskStatus.IN_PROGRESS);
        original.markCompleteBy("alice");
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF",
                List.of("bob"), TaskStatus.IN_PROGRESS);
        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saves);
        Task saved = dao.board.getTask("task1");
        assertEquals(List.of("bob"), saved.getAssignees());
        assertFalse(saved.isCompletedBy("alice"));
        assertTrue(saved.getCompletedBy().isEmpty());
        assertTrue(original.isCompletedBy("alice"));
    }

    @Test
    void preservesCompletionRecordsOfRemainingAssignees() {
        original.update("Original", "", null, "#FFFFFF",
                List.of("alice", "bob"), TaskStatus.IN_PROGRESS);
        original.markCompleteBy("alice");
        original.markCompleteBy("bob");
        execute("alice", "board1", "task1", "Updated", null, "#FFFFFF",
                List.of("bob", "viewer"), TaskStatus.IN_PROGRESS);
        assertNull(presenter.error);
        assertNotNull(presenter.success);
        assertEquals(1, dao.saves);
        Task saved = dao.board.getTask("task1");
        assertEquals(Set.of("bob"), saved.getCompletedBy());
        assertFalse(saved.isFullyComplete());
        assertEquals(TaskStatus.IN_PROGRESS, saved.getStatus());
    }

    @Test
    void inputDataDefensivelyCopiesAssignees() {
        List<String> assignees = new ArrayList<>(List.of("alice"));
        EditTaskInputData input = new EditTaskInputData("alice", "board1", "task1",
                "Updated", "", null, "#FFFFFF", assignees, TaskStatus.TO_DO);
        assignees.add("bob");
        assertEquals(List.of("alice"), input.getAssignees());
        assertThrows(UnsupportedOperationException.class,
                () -> input.getAssignees().add("viewer"));
    }
}
