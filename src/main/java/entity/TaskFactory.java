package entity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Factory responsible for creating new Task entities.
 */
public class TaskFactory {

    /**
     * Creates a new task with a unique ID and an initial TO_DO status.
     *
     * @param name the name of the task
     * @param description the description of the task
     * @param deadline the deadline, or null if none exists
     * @param colour the colour associated with the task
     * @param assignees the usernames assigned to the task
     * @return a newly created task
     * @throws IllegalArgumentException if the task information is invalid
     */
    public Task create(String name, String description, LocalDate deadline,
                       String colour, List<String> assignees) {
        String id = UUID.randomUUID().toString();

        return new Task(id, name, description, deadline, colour, assignees);
    }
}
