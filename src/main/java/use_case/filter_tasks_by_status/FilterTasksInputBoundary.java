package use_case.filter_tasks_by_status;

/**
 * Input boundary for the FilterTasks use case.
 * Implemented by FilterTasksInteractor.
 */
public interface FilterTasksInputBoundary {
    /**
     * Executes the filter tasks use case.
     * @param in the input data containing boardId, status, and optional members
     */
    void execute(FilterTasksInputData in);

}
