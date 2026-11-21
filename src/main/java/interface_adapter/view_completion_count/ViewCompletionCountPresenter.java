package interface_adapter.view_completion_count;

import use_case.view_completion_count.ViewCompletionCountOutputBoundary;
import use_case.view_completion_count.ViewCompletionCountOutputData;
/**
 * Presenter for the ViewCompletionCount use case.
 * Converts output data into a view model for the view.
 */
public class ViewCompletionCountPresenter implements ViewCompletionCountOutputBoundary{
    private final ViewCompletionCountViewModel viewModel;
    public ViewCompletionCountPresenter(ViewCompletionCountViewModel viewModel){
        this.viewModel = viewModel;
    }
    /**
     * Called when completion count is retrieved successfully.
     * @param out the output data containing completed and total counts
     */
    @Override
    public void prepareSuccessView(ViewCompletionCountOutputData out){
        viewModel.setCompleted(out.getCompleted());
        viewModel.setTotal(out.getTotal());
    }
    /**
     * Called when retrieval fails.
     * @param error a message describing what went wrong
     */
    public void prepareFailView(String error){
        viewModel.setError(error);
    }
}