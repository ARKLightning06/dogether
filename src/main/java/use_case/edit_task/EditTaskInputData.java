package use_case.edit_task;

import entity.TaskStatus;
import java.time.LocalDate;
import java.util.List;

/**
 * The input data for the Edit Task use case.
 */
public class EditTaskInputData {
    private final String username;
    private final String boardId;
    private final String taskId;
    private final String name;
    private final String description;
    private final LocalDate deadline;
    private final String colour;
    private final List<String> assignees;
    private final TaskStatus status;

    /**
     * Constructs the input data for editing a task.
     *
     * @param username the username of the user editing the task
     * @param boardId the ID of the board containing the task
     * @param taskId the ID of the task being edited
     * @param name the updated task name
     * @param description the updated task description
     * @param deadline the updated deadline, or null if none exists
     * @param colour the updated task colour
     * @param assignees the updated list of assigned usernames
     * @param status the updated task status
     */
    public EditTaskInputData(String username, String boardId, String taskId,
                             String name, String description, LocalDate deadline, String colour,
                             List<String> assignees, TaskStatus status) {
        this.username = username;
        this.boardId = boardId;
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.colour = colour;
        this.assignees = assignees == null ? null : List.copyOf(assignees);
        this.status = status;
    }

    /**
     * Returns the username of the user editing the task.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the board ID.
     *
     * @return the board ID
     */
    public String getBoardId() {
        return boardId;
    }

    /**
     * Returns the task ID.
     *
     * @return the task ID
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Returns the updated task name.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the updated task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the updated task deadline.
     *
     * @return the deadline, or null if none exists
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Returns the updated task colour.
     *
     * @return the task colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * Returns the updated assignee usernames.
     *
     * @return the assignees, or null if none were provided
     */
    public List<String> getAssignees() {
        return assignees;
    }

    /**
     * Returns the updated task status.
     *
     * @return the task status
     */
    public TaskStatus getStatus() {
        return status;
    }
}
