
package use_case.edit_task;

/**
 * The output boundary for the Edit Task use case.
 */
public interface EditTaskOutputBoundary {

    /**
     * Prepares the presentation state after successfully editing a task.
     *
     * @param outputData the information about the edited task
     */
    void prepareSuccessView(EditTaskOutputData outputData);

    /**
     * Prepares the presentation state when editing a task fails.
     *
     * @param error the message explaining why editing failed
     */
    void prepareFailView(String error);
}
