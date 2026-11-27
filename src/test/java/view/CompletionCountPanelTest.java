package view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import interface_adapter.board.BoardViewModel;
import interface_adapter.view_completion_count.ViewCompletionCountController;
import interface_adapter.view_completion_count.ViewCompletionCountPresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.view_completion_count.ViewCompletionCountInputData;
import use_case.view_completion_count.ViewCompletionCountOutputData;

/**
 * Tests that CompletionCountPanel asks for the right count and shows the label
 * and progress from BoardViewModel.
 */
class CompletionCountPanelTest {
    private BoardViewModel viewModel;
    private ViewCompletionCountInputData lastInput;
    private CompletionCountPanel panel;

    @BeforeEach
    void setUp() {
        viewModel = new BoardViewModel();
        viewModel.getState().setBoardId("board-1");
        final ViewCompletionCountController controller =
                new ViewCompletionCountController(in -> lastInput = in);
        panel = new CompletionCountPanel(viewModel, controller);
        panel.setCurrentUsername("mavi");
    }

    @Test
    void refreshAsksForTheCurrentUsersCount() {
        panel.refresh();

        assertEquals("mavi", lastInput.getUsername());
        assertEquals("board-1", lastInput.getBoardId());
    }

    @Test
    void showsTheLabelAndProgress() {
        new ViewCompletionCountPresenter(viewModel).prepareSuccessView(new ViewCompletionCountOutputData(5, 2));

        assertEquals("2 of 5 tasks completed", panel.getCountLabel().getText());
        assertEquals(40, panel.getBarPercent());
    }

    @Test
    void noTasksShowsZeroAndAnEmptyBar() {
        new ViewCompletionCountPresenter(viewModel).prepareSuccessView(new ViewCompletionCountOutputData(0, 0));

        assertEquals("0 of 0 tasks completed", panel.getCountLabel().getText());
        assertEquals(0, panel.getBarPercent());
    }

    @Test
    void failureShowsZeroInsteadOfTheError() {
        new ViewCompletionCountPresenter(viewModel).prepareSuccessView(new ViewCompletionCountOutputData(5, 2));
        new ViewCompletionCountPresenter(viewModel).prepareFailView("Board not found");

        assertEquals(0, panel.getBarPercent());
    }
}
