package interface_adapter.view_completion_count;

import use_case.view_completion_count.ViewCompletionCountInputData;
import use_case.view_completion_count.ViewCompletionCountInputBoundary;

/**
 * Controller for the ViewCompletionCount use case.
 * Receives input from the view and delegates to the interactor.
 */
public class ViewCompletionCountController {
    private final ViewCompletionCountInputBoundary interactor;
    public ViewCompletionCountController(ViewCompletionCountInputBoundary interactor){
        this.interactor = interactor;
    }
    /**
     * Executes the ViewCompletionCount use case.
     * @param username the logged-in user's username
     * @param boardId the ID of the board to check
     */
    public void execute(String username, String boardId){
        ViewCompletionCountInputData in = new ViewCompletionCountInputData(username, boardId);
        interactor.execute(in);
    }
}