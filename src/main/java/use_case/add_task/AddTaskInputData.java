package use_case.add_task;

import java.time.LocalDate;
import java.util.List;

/**
 * The input data for the Add Task use case.
 */
public class AddTaskInputData {
    private final String username;
    private final String boardId;
    private final String name;
    private final String description;
    private final LocalDate deadline;
    private final String colour;
    private final List<String> assignees;

    /**
     * Constructs the input data for creating a task.
     *
     * @param username the username of the user creating the task
     * @param boardId the ID of the board receiving the task
     * @param name the task name
     * @param description the task description
     * @param deadline the task deadline, or null if none
     * @param colour the task colour
     * @param assignees the usernames assigned to the task
     */
    public AddTaskInputData(String username, String boardId, String name,
                            String description, LocalDate deadline, String colour,
                            List<String> assignees) {
        this.username = username;
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.colour = colour;
        this.assignees = assignees == null ? null : List.copyOf(assignees);
    }

    /**
     * Returns the username of the user creating the task.
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
     * Returns the task name.
     *
     * @return the task name
     */
    public String getName() {
        return name;
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
     * Returns the task deadline.
     *
     * @return the deadline, or null if none
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Returns the task colour.
     *
     * @return the colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * Returns the usernames assigned to the task.
     *
     * @return the assignees
     */
    public List<String> getAssignees() {
        return assignees;
    }
}
