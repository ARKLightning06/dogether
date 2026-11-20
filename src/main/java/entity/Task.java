package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a task belonging to a DoGether board.
 *
 * <p>A task can be assigned to multiple users. Each assigned user can
 * independently mark the task as complete.
 */
public class Task {
    private final String id;
    private String name;
    private String description;
    private LocalDate deadline;
    private String colour;
    private TaskStatus status;
    private List<String> assignees;
    private final Set<String> completedBy;

    /**
     * Constructs a task with the given information.
     *
     * @param id the unique identifier of the task
     * @param name the name of the task
     * @param description the description of the task
     * @param deadline the deadline of the task, or null if none exists
     * @param colour the colour associated with the task
     * @param assignees the usernames assigned to the task
     * @throws IllegalArgumentException if the task information is invalid
     */
    public Task(String id, String name, String description,
                LocalDate deadline, String colour, List<String> assignees) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Task ID cannot be empty.");
        }

        validate(name, description, colour, assignees);

        this.id = id;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.colour = colour;
        this.status = TaskStatus.TO_DO;
        this.assignees = new ArrayList<>(assignees);
        this.completedBy = new HashSet<>();
    }

    /**
     * Returns whether the given user is assigned to this task.
     *
     * @param username the username to check
     * @return true if the user is assigned to this task
     */
    public boolean isAssignedTo(String username) {
        return assignees.contains(username);
    }

    /**
     * Returns whether the given user has completed this task.
     *
     * @param username the username to check
     * @return true if the user has completed this task
     */
    public boolean isCompletedBy(String username) {
        return completedBy.contains(username);
    }

    /**
     * Marks this task as complete for the given user.
     *
     * <p>If every assignee has completed the task, its status
     * automatically changes to DONE.
     *
     * @param username the username completing the task
     * @throws IllegalArgumentException if the user is not assigned
     * @throws IllegalStateException if the user has already completed it
     */
    public void markCompleteBy(String username) {
        if (!isAssignedTo(username)) {
            throw new IllegalArgumentException(
                    "User is not assigned to this task.");
        }

        if (isCompletedBy(username)) {
            throw new IllegalStateException(
                    "User has already completed this task.");
        }

        completedBy.add(username);

        if (isFullyComplete()) {
            status = TaskStatus.DONE;
        }
    }

    /**
     * Returns whether every assigned user has completed this task.
     *
     * <p>A task with no assignees is not considered fully complete.
     *
     * @return true if all assignees have completed the task
     */
    public boolean isFullyComplete() {
        return !assignees.isEmpty()
                && completedBy.containsAll(assignees);
    }

    /**
     * Updates the editable attributes of this task.
     *
     * <p>Completion records are preserved for users who remain assigned.
     * Records belonging to removed assignees are discarded.
     *
     * @param name the updated task name
     * @param description the updated task description
     * @param deadline the updated deadline, or null for no deadline
     * @param colour the updated task colour
     * @param assignees the updated list of assigned usernames
     * @param status the updated workflow status
     * @throws IllegalArgumentException if the updated information is invalid
     */
    public void update(String name, String description, LocalDate deadline,
                       String colour, List<String> assignees, TaskStatus status) {
        validate(name, description, colour, assignees);
        Objects.requireNonNull(status, "Task status cannot be null.");

        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.colour = colour;
        this.assignees = new ArrayList<>(assignees);
        this.status = status;

        completedBy.retainAll(this.assignees);

        if (isFullyComplete()) {
            this.status = TaskStatus.DONE;
        } else if (this.status == TaskStatus.DONE) {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    /**
     * Validates the editable attributes of a task.
     *
     * @param name the task name
     * @param description the task description
     * @param colour the task colour
     * @param assignees the assigned usernames
     * @throws IllegalArgumentException if any attribute is invalid
     */
    private static void validate(String name, String description,
                                 String colour, List<String> assignees) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Task name cannot be empty.");
        }

        if (description == null) {
            throw new IllegalArgumentException(
                    "Task description cannot be null.");
        }

        if (colour == null || colour.isBlank()) {
            throw new IllegalArgumentException("Task colour cannot be empty.");
        }

        if (assignees == null) {
            throw new IllegalArgumentException(
                    "Task assignees cannot be null.");
        }

        Set<String> uniqueAssignees = new HashSet<>();

        for (String username : assignees) {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException(
                        "Assignee username cannot be empty.");
            }

            if (!uniqueAssignees.add(username)) {
                throw new IllegalArgumentException(
                        "Task cannot contain duplicate assignees.");
            }
        }
    }

    /**
     * Returns the unique identifier of this task.
     *
     * @return the task ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of this task.
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of this task.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the deadline of this task.
     *
     * @return the deadline, or null if none exists
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Returns the colour associated with this task.
     *
     * @return the task colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * Returns the current status of this task.
     *
     * @return the task status
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Returns an unmodifiable snapshot of assigned usernames.
     *
     * @return the assigned usernames
     */
    public List<String> getAssignees() {
        return List.copyOf(assignees);
    }

    /**
     * Returns an unmodifiable snapshot of usernames that completed the task.
     *
     * @return the usernames that completed this task
     */
    public Set<String> getCompletedBy() {
        return Set.copyOf(completedBy);
    }
}
