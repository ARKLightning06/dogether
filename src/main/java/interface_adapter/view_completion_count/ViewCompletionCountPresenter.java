package interface_adapter.view_completion_count;

import interface_adapter.board.BoardViewModel;
import use_case.view_completion_count.ViewCompletionCountOutputBoundary;
import use_case.view_completion_count.ViewCompletionCountOutputData;

public class ViewCompletionCountPresenter implements ViewCompletionCountOutputBoundary {
    private final BoardViewModel boardViewModel;

    public ViewCompletionCountPresenter(BoardViewModel boardViewModel) {
        this.boardViewModel = boardViewModel;
    }

    @Override
    public void prepareSuccessView(ViewCompletionCountOutputData out) {
        boardViewModel.setError("");
        boardViewModel.setCompletedCount(out.getCompleted());
        boardViewModel.setTotalCount(out.getTotal());
        boardViewModel.setCompletionLabel(out.getCompleted() + " of " + out.getTotal() + " tasks completed");
        boardViewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        boardViewModel.setCompletedCount(0);
        boardViewModel.setTotalCount(0);
        boardViewModel.setError(error);
        boardViewModel.firePropertyChanged();
    }
}
