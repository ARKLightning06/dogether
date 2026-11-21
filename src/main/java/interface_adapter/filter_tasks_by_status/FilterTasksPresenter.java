package interface_adapter.filter_tasks_by_status;

import use_case.filter_tasks_by_status.FilterTasksOutputData;
import use_case.filter_tasks_by_status.FilterTasksOutputBoundary;


/**
 * Presenter for the FilterTasks use case.
 * Converts output data into a view model for the view.
 */

public class FilterTasksPresenter implements FilterTasksOutputBoundary {
    private final FilterTasksViewModel viewModel;
    public FilterTasksPresenter(FilterTasksViewModel viewmodel){
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(FilterTasksOutputData out){
        viewModel.setTaskIds(out.getTaskIds());
        viewModel.setTaskNames(out.getTaskNames());
    }
    @Override
    public void prepareFailView(String error){
        viewModel.setError(error);
    }
}