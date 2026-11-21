package interface_adapter.filter_tasks_by_status;

import interface_adapter.board.BoardViewModel;
import use_case.filter_tasks_by_status.FilterTasksOutputBoundary;
import use_case.filter_tasks_by_status.FilterTasksOutputData;

/**
 * Presenter for the FilterTasks use case.
 * Converts output data into the shared BoardViewModel for the view.
 */
public class FilterTasksPresenter implements FilterTasksOutputBoundary {
    private final BoardViewModel boardViewModel;

    public FilterTasksPresenter(BoardViewModel boardViewModel) {
        this.boardViewModel = boardViewModel;
    }

    @Override
    public void prepareSuccessView(FilterTasksOutputData out) {
        boardViewModel.setTaskIds(out.getTaskIds());
        boardViewModel.setTaskNames(out.getTaskNames());
    }

    @Override
    public void prepareFailView(String error) {
        boardViewModel.setError(error);
    }
}