
package use_case.edit_task;

/**
 * The input boundary for the Edit Task use case.
 */
public interface EditTaskInputBoundary {

    /**
     * Executes the Edit Task use case.
     *
     * @param inputData the information needed to edit the task
     */
    void execute(EditTaskInputData inputData);
}
