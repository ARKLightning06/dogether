package interface_adapter.filter_tasks_by_status;

import static org.junit.jupiter.api.Assertions.assertEquals;

import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.filter_tasks_by_status.FilterTasksOutputData;

/**
 * Unit tests for FilterTasksPresenter.
 * Checks that the results end up in the BoardViewModel and that the
 * view is told to redraw exactly once per call.
 */
class FilterTasksPresenterTest {
    private BoardViewModel viewModel;
    private FilterTasksPresenter presenter;
    private int fireCount;

    @BeforeEach
    void setUp() {
        viewModel = new BoardViewModel();
        presenter = new FilterTasksPresenter(viewModel);
        fireCount = 0;
        viewModel.addPropertyChangeListener(evt -> fireCount++);
    }

    @Test
    void successPutsMatchingTasksInState() {
        presenter.prepareSuccessView(new FilterTasksOutputData(
                List.of("t1", "t2"), List.of("Clean fridge", "Buy milk")));

        BoardState state = viewModel.getState();
        assertEquals(List.of("t1", "t2"), state.getTaskIds());
        assertEquals(List.of("Clean fridge", "Buy milk"), state.getTaskNames());
        assertEquals("", state.getError());
    }

    @Test
    void successFiresExactlyOnce() {
        presenter.prepareSuccessView(new FilterTasksOutputData(List.of("t1"), List.of("Clean fridge")));

        assertEquals(1, fireCount);
    }

    @Test
    void successWithNoMatchesGivesEmptyLists() {
        presenter.prepareSuccessView(new FilterTasksOutputData(
                Collections.emptyList(), Collections.emptyList()));

        assertEquals(Collections.emptyList(), viewModel.getState().getTaskIds());
        assertEquals(Collections.emptyList(), viewModel.getState().getTaskNames());
        assertEquals(1, fireCount);
    }

    @Test
    void failSetsErrorAndFiresExactlyOnce() {
        presenter.prepareFailView("Board not found");

        assertEquals("Board not found", viewModel.getState().getError());
        assertEquals(1, fireCount);
    }

    @Test
    void failKeepsTheTasksAlreadyShown() {
        presenter.prepareSuccessView(new FilterTasksOutputData(List.of("t1"), List.of("Clean fridge")));
        presenter.prepareFailView("You are not a member of this board");

        assertEquals(List.of("t1"), viewModel.getState().getTaskIds());
        assertEquals("You are not a member of this board", viewModel.getState().getError());
    }

    @Test
    void successAfterFailClearsTheError() {
        presenter.prepareFailView("Board not found");
        presenter.prepareSuccessView(new FilterTasksOutputData(List.of("t1"), List.of("Clean fridge")));

        assertEquals("", viewModel.getState().getError());
        assertEquals(2, fireCount);
    }
}
