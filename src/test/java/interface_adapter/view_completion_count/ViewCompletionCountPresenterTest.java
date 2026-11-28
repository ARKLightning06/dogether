package interface_adapter.view_completion_count;

import static org.junit.jupiter.api.Assertions.assertEquals;

import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.view_completion_count.ViewCompletionCountOutputData;

/**
 * Unit tests for ViewCompletionCountPresenter.
 * Checks the counts, the "X of Y tasks completed" label, and that the
 * view is told to redraw exactly once per call.
 */
class ViewCompletionCountPresenterTest {
    private BoardViewModel viewModel;
    private ViewCompletionCountPresenter presenter;
    private int fireCount;

    @BeforeEach
    void setUp() {
        viewModel = new BoardViewModel();
        presenter = new ViewCompletionCountPresenter(viewModel);
        fireCount = 0;
        viewModel.addPropertyChangeListener(evt -> fireCount++);
    }

    @Test
    void successSetsCountsAndLabel() {
        presenter.prepareSuccessView(new ViewCompletionCountOutputData(5, 2));

        BoardState state = viewModel.getState();
        assertEquals(2, state.getCompletedCount());
        assertEquals(5, state.getTotalCount());
        assertEquals("2 of 5 tasks completed", state.getCompletionLabel());
        assertEquals("", state.getError());
    }

    @Test
    void successFiresExactlyOnce() {
        presenter.prepareSuccessView(new ViewCompletionCountOutputData(5, 2));

        assertEquals(1, fireCount);
    }

    @Test
    void noAssignedTasksShowsZeroOfZero() {
        presenter.prepareSuccessView(new ViewCompletionCountOutputData(0, 0));

        assertEquals("0 of 0 tasks completed", viewModel.getState().getCompletionLabel());
    }

    @Test
    void allDoneShowsFullCount() {
        presenter.prepareSuccessView(new ViewCompletionCountOutputData(3, 3));

        assertEquals("3 of 3 tasks completed", viewModel.getState().getCompletionLabel());
    }

    @Test
    void failResetsCountsSetsErrorAndFiresOnce() {
        presenter.prepareSuccessView(new ViewCompletionCountOutputData(5, 2));
        fireCount = 0;

        presenter.prepareFailView("Board not found");

        BoardState state = viewModel.getState();
        assertEquals(0, state.getCompletedCount());
        assertEquals(0, state.getTotalCount());
        assertEquals("Board not found", state.getError());
        assertEquals(1, fireCount);
    }

    @Test
    void successAfterFailClearsTheError() {
        presenter.prepareFailView("Board not found");
        presenter.prepareSuccessView(new ViewCompletionCountOutputData(4, 1));

        assertEquals("", viewModel.getState().getError());
        assertEquals("1 of 4 tasks completed", viewModel.getState().getCompletionLabel());
    }
}
