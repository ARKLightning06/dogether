
package interface_adapter.edit_task;

import entity.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import use_case.edit_task.EditTaskInputBoundary;
import use_case.edit_task.EditTaskInputData;

/**
 * Controller for the Edit Task use case.
 */
public class EditTaskController {
    private final EditTaskInputBoundary interactor;

    /**
     * Constructs an Edit Task controller.
     *
     * @param interactor the input boundary for the Edit Task use case
     */
    public EditTaskController(EditTaskInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the Edit Task use case.
     *
     * @param username the username of the user editing the task
     * @param boardId the ID of the board containing the task
     * @param taskId the ID of the task being edited
     * @param name the updated task name
     * @param description the updated task description
     * @param deadline the updated task deadline
     * @param colour the updated task colour
     * @param assignees the updated list of assigned usernames
     * @param status the updated task status
     */
    public void execute(String username, String boardId, String taskId,
                        String name, String description, LocalDate deadline, String colour,
                        List<String> assignees, TaskStatus status) {
        EditTaskInputData inputData = new EditTaskInputData(
                username, boardId, taskId, name, description,
                deadline, colour, assignees, status);

        interactor.execute(inputData);
    }
}
