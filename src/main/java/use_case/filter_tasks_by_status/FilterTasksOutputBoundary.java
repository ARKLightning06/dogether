package use_case.filter_tasks_by_status;

/**
 * Output boundary for the FilterTasks use case.
 * Implemented by FilterTasksPresenter.
 */
public interface FilterTasksOutputBoundary {
    /**
     * Called when filtering succeeds.
     * @param out the output data containing matching task IDs and names
     */
    void prepareSuccessView(FilterTasksOutputData out);
    /**
     * Called when filtering fails (e.g. board not found).
     * @param error a message describing what went wrong
     */
    void prepareFailView(String error);
}
