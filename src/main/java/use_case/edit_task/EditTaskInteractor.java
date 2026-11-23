package use_case.edit_task;

import entity.Board;
import entity.Task;
import java.util.List;

/**
 * Interactor for the Edit Task use case.
 *
 * <p>Validates editing permissions, updates an existing task,
 * and saves the modified board.
 */
public class EditTaskInteractor implements EditTaskInputBoundary {
    private final EditTaskDataAccessInterface dataAccess;
    private final EditTaskOutputBoundary presenter;

    /**
     * Constructs an Edit Task interactor.
     *
     * @param dataAccess the interface for retrieving and saving boards
     * @param presenter the output boundary for reporting the result
     */
    public EditTaskInteractor(EditTaskDataAccessInterface dataAccess,
                              EditTaskOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the Edit Task use case.
     *
     * <p>The requesting user must have editing permissions on the board,
     * and all task assignees must be members of that board.
     *
     * @param inputData the information needed to edit the task
     */
    @Override
    public void execute(EditTaskInputData inputData) {
        final Board board;

        try {
            board = dataAccess.getBoard(inputData.getBoardId());
        } catch (RuntimeException exception) {
            presenter.prepareFailView("Unable to load the board.");
            return;
        }

        if (board == null) {
            presenter.prepareFailView("Board not found.");
            return;
        }

        if (!board.hasMember(inputData.getUsername())
                || !board.canEdit(inputData.getUsername())) {
            presenter.prepareFailView(
                    "You do not have permission to edit tasks on this board.");
            return;
        }

        Task task = board.getTask(inputData.getTaskId());

        if (task == null) {
            presenter.prepareFailView("Task not found.");
            return;
        }

        List<String> assignees = inputData.getAssignees();

        if (assignees == null) {
            presenter.prepareFailView("Task assignees cannot be null.");
            return;
        }

        for (String username : assignees) {
            if (username == null || !board.hasMember(username)) {
                presenter.prepareFailView(
                        "All task assignees must be board members.");
                return;
            }
        }

        try {
            task.update(
                    inputData.getName(),
                    inputData.getDescription(),
                    inputData.getDeadline(),
                    inputData.getColour(),
                    assignees,
                    inputData.getStatus());
        } catch (IllegalArgumentException | NullPointerException exception) {
            presenter.prepareFailView(exception.getMessage());
            return;
        }

        try {
            dataAccess.saveBoard(board);
        } catch (RuntimeException exception) {
            presenter.prepareFailView("Unable to save the updated task.");
            return;
        }

        EditTaskOutputData outputData = new EditTaskOutputData(
                task.getId(), task.getName());

        presenter.prepareSuccessView(outputData);
    }
}
