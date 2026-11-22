
package interface_adapter.add_task;

import java.time.LocalDate;
import java.util.List;
import use_case.add_task.AddTaskInputBoundary;
import use_case.add_task.AddTaskInputData;

/**
 * Controller for the Add Task use case.
 */
public class AddTaskController {
    private final AddTaskInputBoundary interactor;

    /**
     * Constructs an Add Task controller.
     *
     * @param interactor the input boundary for the Add Task use case
     */
    public AddTaskController(AddTaskInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the Add Task use case.
     *
     * @param username the username of the user creating the task
     * @param boardId the ID of the board receiving the task
     * @param name the name of the task
     * @param description the task description
     * @param deadline the deadline, or null if none exists
     * @param colour the colour of the task
     * @param assignees the usernames assigned to the task
     */
    public void execute(String username, String boardId, String name,
                        String description, LocalDate deadline, String colour,
                        List<String> assignees) {
        AddTaskInputData inputData = new AddTaskInputData(
                username, boardId, name, description, deadline, colour, assignees);

        interactor.execute(inputData);
    }
}
