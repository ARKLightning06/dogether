package interface_adapter.edit_task;

import interface_adapter.ViewManagerModel;
import interface_adapter.board.BoardViewModel;
import java.util.List;
import use_case.edit_task.EditTaskOutputData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Edit Task presenter.
 */
class EditTaskPresenterTest {
    private EditTaskViewModel editor;
    private BoardViewModel board;
    private ViewManagerModel navigation;
    private EditTaskPresenter presenter;
    private int editorEvents;
    private int boardEvents;
    private int navigationEvents;

    @BeforeEach
    void setUp() {
        editor = new EditTaskViewModel();
        board = new BoardViewModel();
        navigation = new ViewManagerModel();
        navigation.setState(editor.getViewName());
        editor.getState().setTaskId("t1");
        editor.getState().setName("Draft");
        board.setTaskIds(List.of("t1", "t2"));
        board.setTaskNames(List.of("Original", "Other"));
        presenter = new EditTaskPresenter(editor, board, navigation);
        editor.addPropertyChangeListener(evt -> editorEvents++);
        board.addPropertyChangeListener(evt -> boardEvents++);
        navigation.addPropertyChangeListener(evt -> navigationEvents++);
    }

    @Test
    void successUpdatesOnlyMatchingTaskAndNavigates() {
        presenter.prepareSuccessView(new EditTaskOutputData("t1", "Updated"));
        assertEquals(List.of("Updated", "Other"), board.getState().getTaskNames());
        assertEquals(List.of("t1", "t2"), board.getState().getTaskIds());
        assertEquals("", editor.getState().getTaskId());
        assertEquals(board.getViewName(), navigation.getState());
        assertEquals(1, editorEvents);
        assertEquals(1, boardEvents);
        assertEquals(1, navigationEvents);
    }

    @Test
    void failurePreservesDraftAndDoesNotNavigate() {
        presenter.prepareFailView("Board not found.");
        assertEquals("Draft", editor.getState().getName());
        assertEquals("t1", editor.getState().getTaskId());
        assertEquals("Board not found.", editor.getState().getError());
        assertEquals(editor.getViewName(), navigation.getState());
        assertEquals(List.of("Original", "Other"), board.getState().getTaskNames());
        assertEquals(1, editorEvents);
        assertEquals(0, boardEvents);
        assertEquals(0, navigationEvents);
    }

    @Test
    void taskNotInVisibleListLeavesListUnchanged() {
        presenter.prepareSuccessView(new EditTaskOutputData("hidden", "Updated"));
        assertEquals(List.of("Original", "Other"), board.getState().getTaskNames());
    }
}
