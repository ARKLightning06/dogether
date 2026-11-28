package use_case.edit_task;

import data_access.InMemoryBoardDataAccessObject;
import entity.Board;
import entity.Permission;
import entity.Task;
import entity.TaskStatus;
import interface_adapter.ViewManagerModel;
import interface_adapter.board.BoardViewModel;
import interface_adapter.edit_task.EditTaskController;
import interface_adapter.edit_task.EditTaskPresenter;
import interface_adapter.edit_task.EditTaskViewModel;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for the Edit Task integration.
 */
class EditTaskIntegrationTest {
    private EditTaskViewModel editor;
    private BoardViewModel boardModel;
    private ViewManagerModel navigation;
    private EditTaskController controller;
    private InMemoryBoardDataAccessObject dao;

    @BeforeEach
    void setUp() {
        dao = new InMemoryBoardDataAccessObject();
        Board board = new Board("b1", "Board", "alice");
        board.addMember("bob", Permission.EDIT);
        board.addTask(new Task("t1", "Original", "", null, "#FFFFFF", List.of("alice")));
        dao.saveBoard(board);
        editor = new EditTaskViewModel();
        editor.getState().setTaskId("t1");
        editor.getState().setName("Draft");
        boardModel = new BoardViewModel();
        boardModel.setTaskIds(List.of("t1"));
        boardModel.setTaskNames(List.of("Original"));
        navigation = new ViewManagerModel();
        navigation.setState(editor.getViewName());
        EditTaskPresenter presenter = new EditTaskPresenter(editor, boardModel, navigation);
        controller = new EditTaskController(new EditTaskInteractor(dao, presenter));
    }

    @Test
    void editUpdatesStoredTaskAndBoardViewModel() {
        controller.execute("alice", "b1", "t1", "Updated", "Description", null,
                "#D4537E", List.of("bob"), TaskStatus.IN_PROGRESS);
        Task saved = dao.getBoard("b1").getTask("t1");
        assertEquals("Updated", saved.getName());
        assertEquals(List.of("bob"), saved.getAssignees());
        assertEquals(TaskStatus.IN_PROGRESS, saved.getStatus());
        assertEquals(List.of("Updated"), boardModel.getState().getTaskNames());
        assertEquals("", editor.getState().getTaskId());
        assertEquals(boardModel.getViewName(), navigation.getState());
    }

    @Test
    void invalidEditKeepsBoardAndShowsError() {
        controller.execute("alice", "b1", "t1", "", "Description", null,
                "#D4537E", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertEquals("Original", dao.getBoard("b1").getTask("t1").getName());
        assertEquals(List.of("Original"), boardModel.getState().getTaskNames());
        assertEquals("Draft", editor.getState().getName());
        assertEquals("Task name cannot be empty.", editor.getState().getError());
        assertEquals(editor.getViewName(), navigation.getState());
    }

    @Test
    void invalidColourKeepsStoredTaskAndEditorOpen() {
        controller.execute("alice", "b1", "t1", "Updated", "Description", null,
                "#GGGGGG", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertEquals("Original", dao.getBoard("b1").getTask("t1").getName());
        assertEquals("#FFFFFF", dao.getBoard("b1").getTask("t1").getColour());
        assertEquals("Enter a valid colour.", editor.getState().getError());
        assertEquals("Draft", editor.getState().getName());
        assertEquals(editor.getViewName(), navigation.getState());
        assertEquals(List.of("Original"), boardModel.getState().getTaskNames());
    }
}
