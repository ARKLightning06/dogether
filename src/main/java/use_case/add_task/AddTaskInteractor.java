package use_case.add_task;

import entity.Board;
import entity.Task;
import entity.TaskFactory;
import java.util.List;

/**
 * Interactor for the Add Task use case.
 *
 * <p>Validates the user's permissions and task assignments, creates
 * the task, and saves it to the corresponding board.
 */
public class AddTaskInteractor implements AddTaskInputBoundary {
    private final AddTaskDataAccessInterface dataAccess;
    private final AddTaskOutputBoundary presenter;
    private final TaskFactory factory;

    /**
     * Constructs an Add Task interactor.
     *
     * @param dataAccess the interface for retrieving and saving boards
     * @param presenter the output boundary for reporting the result
     * @param factory the factory used to create tasks
     */
    public AddTaskInteractor(AddTaskDataAccessInterface dataAccess,
                             AddTaskOutputBoundary presenter, TaskFactory factory) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.factory = factory;
    }

    /**
     * Executes the Add Task use case.
     *
     * <p>The requesting user must have permission to edit the board.
     * All task assignees must also be members of the board.
     *
     * @param inputData the information needed to create the task
     */
    @Override
    public void execute(AddTaskInputData inputData) {
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
                    "You do not have permission to add tasks to this board.");
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

        final Task task;

        try {
            task = factory.create(
                    inputData.getName(),
                    inputData.getDescription(),
                    inputData.getDeadline(),
                    inputData.getColour(),
                    assignees);
        } catch (IllegalArgumentException exception) {
            presenter.prepareFailView(exception.getMessage());
            return;
        }

        try {
            board.addTask(task);
            dataAccess.saveBoard(board);
        } catch (RuntimeException exception) {
            presenter.prepareFailView("Unable to save the task.");
            return;
        }

        AddTaskOutputData outputData = new AddTaskOutputData(
                task.getId(), task.getName());

        presenter.prepareSuccessView(outputData);
    }
}
