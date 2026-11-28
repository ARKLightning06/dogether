
package use_case.add_task;

/**
 * The input boundary for the Add Task use case.
 */
public interface AddTaskInputBoundary {

    /**
     * Executes the Add Task use case.
     *
     * @param inputData the information needed to create the task
     */
    void execute(AddTaskInputData inputData);
}
