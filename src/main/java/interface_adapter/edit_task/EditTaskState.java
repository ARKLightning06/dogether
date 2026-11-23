package interface_adapter.edit_task;

import entity.TaskStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the current state of the task editor.
 *
 * <p>The state is shared between the Add Task and Edit Task use cases.
 */
public class EditTaskState {
    private String taskId = "";
    private String name = "";
    private String description = "";
    private LocalDate deadline;
    private String colour = "#FFFFFF";
    private List<String> assignees = new ArrayList<>();
    private TaskStatus status = TaskStatus.TO_DO;
    private String error = "";

    /**
     * Returns the ID of the task being edited.
     *
     * @return the task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets the ID of the task being edited.
     *
     * @param taskId the task ID
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * Returns the task name.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the task name.
     *
     * @param name the task name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the task description.
     *
     * @param description the task description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the task deadline.
     *
     * @return the deadline, or null if none exists
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Sets the task deadline.
     *
     * @param deadline the task deadline
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * Returns the selected task colour.
     *
     * @return the task colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * Sets the selected task colour.
     *
     * @param colour the task colour
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Returns the assigned usernames.
     *
     * @return a copy of the assignee list
     */
    public List<String> getAssignees() {
        return new ArrayList<>(assignees);
    }

    /**
     * Sets the assigned usernames.
     *
     * @param assignees the usernames assigned to the task
     */
    public void setAssignees(List<String> assignees) {
        this.assignees = new ArrayList<>(assignees);
    }

    /**
     * Returns the task status.
     *
     * @return the task status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the task status.
     *
     * @param status the task status
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Returns the current error message.
     *
     * @return the error message
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the current error message.
     *
     * @param error the error message
     */
    public void setError(String error) {
        this.error = error;
    }
}
