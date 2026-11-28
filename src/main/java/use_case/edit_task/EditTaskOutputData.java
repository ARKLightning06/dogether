package use_case.edit_task;

/**
 * The output data for a successfully edited task.
 */
public class EditTaskOutputData {
    private final String taskId;
    private final String name;

    /**
     * Constructs the output data for the Edit Task use case.
     *
     * @param taskId the ID of the edited task
     * @param name the updated name of the task
     */
    public EditTaskOutputData(String taskId, String name) {
        this.taskId = taskId;
        this.name = name;
    }

    /**
     * Returns the ID of the edited task.
     *
     * @return the task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns the updated name of the task.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }
}
