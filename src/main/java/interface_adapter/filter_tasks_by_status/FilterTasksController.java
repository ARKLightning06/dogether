package interface_adapter.filter_tasks_by_status;

import entity.TaskStatus;
import java.util.List;
import use_case.filter_tasks_by_status.FilterTasksInputBoundary;
import use_case.filter_tasks_by_status.FilterTasksInputData;

/**
 * Controller for the FilterTasks use case.
 * Receives input from the view and delegates to the interactor.
 */
public class FilterTasksController {
    private final FilterTasksInputBoundary interactor;

    public FilterTasksController(FilterTasksInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the filter tasks use case.
     * @param username the logged-in user's username
     * @param boardId the ID of the board whose tasks to filter
     * @param status the status to filter by
     * @param members the list of member usernames on the board
     */
    public void execute(String username, String boardId, TaskStatus status, List<String> members) {
        FilterTasksInputData in = new FilterTasksInputData(username, boardId, status, members);
        interactor.execute(in);
    }
}