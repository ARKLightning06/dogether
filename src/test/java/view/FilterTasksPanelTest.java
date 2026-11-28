package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.TaskStatus;
import interface_adapter.board.BoardViewModel;
import interface_adapter.filter_tasks_by_status.FilterTasksController;
import interface_adapter.filter_tasks_by_status.FilterTasksPresenter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.filter_tasks_by_status.FilterTasksInputData;
import use_case.filter_tasks_by_status.FilterTasksOutputData;

/**
 * Tests that FilterTasksPanel sends the right input to the controller and shows
 * what the presenter puts in BoardViewModel.
 */
class FilterTasksPanelTest {
    private BoardViewModel viewModel;
    private FilterTasksInputData lastInput;
    private FilterTasksPanel panel;

    @BeforeEach
    void setUp() {
        viewModel = new BoardViewModel();
        viewModel.getState().setBoardId("board-1");
        final FilterTasksController controller = new FilterTasksController(in -> lastInput = in);
        panel = new FilterTasksPanel(viewModel, controller);
        panel.setCurrentUsername("mavi");
    }

    @Test
    void choosingAStatusRunsTheFilter() {
        panel.getStatusBox().setSelectedItem(TaskStatus.DONE);

        assertEquals("mavi", lastInput.getUsername());
        assertEquals("board-1", lastInput.getBoardId());
        assertEquals(TaskStatus.DONE, lastInput.getStatus());
        assertTrue(lastInput.getMembers().isEmpty());
    }

    @Test
    void onlyMyTasksSendsTheCurrentUserAsTheMember() {
        panel.getOnlyMineBox().doClick();

        assertEquals(List.of("mavi"), lastInput.getMembers());
    }

    @Test
    void showsTheMatchingTasks() {
        final FilterTasksPresenter presenter = new FilterTasksPresenter(viewModel);
        presenter.prepareSuccessView(new FilterTasksOutputData(
                List.of("t1", "t2"), List.of("Clean fridge", "Buy milk")));

        assertEquals(2, panel.getResults().size());
        assertEquals("Clean fridge", panel.getResults().get(0));
        assertEquals("Buy milk", panel.getResults().get(1));
    }

    @Test
    void showsTheErrorWhenFilteringFails() {
        final FilterTasksPresenter presenter = new FilterTasksPresenter(viewModel);
        presenter.prepareFailView("You are not a member of this board");

        assertEquals("You are not a member of this board", panel.getErrorLabel().getText());
    }

    @Test
    void statusesAreShownInPlainWords() {
        assertEquals("To do", FilterTasksPanel.readable(TaskStatus.TO_DO));
        assertEquals("In progress", FilterTasksPanel.readable(TaskStatus.IN_PROGRESS));
        assertEquals("Done", FilterTasksPanel.readable(TaskStatus.DONE));
    }
}
