
package use_case.add_task;

/**
 * The output boundary for the Add Task use case.
 */
public interface AddTaskOutputBoundary {

    /**
     * Prepares the presentation state after a task is created.
     *
     * @param outputData the information about the created task
     */
    void prepareSuccessView(AddTaskOutputData outputData);

    /**
     * Prepares the presentation state when task creation fails.
     *
     * @param error the error message explaining the failure
     */
    void prepareFailView(String error);
}
