package interface_adapter.filter_tasks_by_status;
import use_case.filter_tasks_by_status.FilterTasksInputBoundary;
import use_case.filter_tasks_by_status.FilterTasksInputData;


/**
 * Controller for the FilterTasks use case.
 * Receives input from the view and delegates to the interactor.
 */

public class FilterTasksController {
    private final FilterTasksInputBoundary interactor;

    public FilterTasksController(FilterTasksInputBoundary interactor){
        this.interactor = interactor;
    }
    /**
     * Executes the filter tasks use case.
     * @param status the status to filter by (e.g. "complete", "incomplete")
     * @param boardId the ID of the board whose tasks to filter
     */
    public void execute(String status, String boardID){
        FilterTasksInputData in = new FilterTasksInputData(status, boardID);
        interactor.execute(in);
    }


}