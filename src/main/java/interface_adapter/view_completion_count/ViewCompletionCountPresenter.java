package interface_adapter.view_completion_count;

import interface_adapter.board.BoardViewModel;
import use_case.view_completion_count.ViewCompletionCountOutputBoundary;
import use_case.view_completion_count.ViewCompletionCountOutputData;

/**
 * Presenter for the ViewCompletionCount use case.
 * Converts output data into the shared BoardViewModel for the view.
 */
public class ViewCompletionCountPresenter implements ViewCompletionCountOutputBoundary {
    private final BoardViewModel boardViewModel;

    public ViewCompletionCountPresenter(BoardViewModel boardViewModel) {
        this.boardViewModel = boardViewModel;
    }

    /**
     * Called when completion count is retrieved successfully.
     * @param out the output data containing completed and total counts
     */
    @Override
    public void prepareSuccessView(ViewCompletionCountOutputData out) {
        boardViewModel.setCompletedCount(out.getCompleted());
        boardViewModel.setTotalCount(out.getTotal());
    }

    /**
     * Called when retrieval fails.
     * @param error a message describing what went wrong
     */
    @Override
    public void prepareFailView(String error) {
        boardViewModel.setError(error);
    }
}