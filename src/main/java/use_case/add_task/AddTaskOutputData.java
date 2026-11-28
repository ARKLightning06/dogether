
package use_case.add_task;

/**
 * The output data for a successfully created task.
 */
public class AddTaskOutputData {
    private final String taskId;
    private final String name;

    /**
     * Constructs the output data for the Add Task use case.
     *
     * @param taskId the ID of the created task
     * @param name the name of the created task
     */
    public AddTaskOutputData(String taskId, String name) {
        this.taskId = taskId;
        this.name = name;
    }

    /**
     * Returns the ID of the created task.
     *
     * @return the task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns the name of the created task.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }
}
