package use_case.filter_tasks_by_status;

import entity.Board;
import entity.Task;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for the FilterTasks use case.
 * Finds the tasks on a board that have a given status and, optionally,
 * are assigned to at least one of the given members.
 *
 * <p>Fails if the board does not exist or the user is not a member of it.
 */
public class FilterTasksInteractor implements FilterTasksInputBoundary {
    private final FilterTasksOutputBoundary presenter;
    private final FilterTasksDataAccessInterface dataAccess;

    /**
     * Creates the interactor.
     *
     * @param presenter  receives the filtered tasks or an error message
     * @param dataAccess used to load the board being filtered
     */
    public FilterTasksInteractor(FilterTasksOutputBoundary presenter,
                                 FilterTasksDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    /**
     * Filters the tasks on the requested board.
     * A task is kept when its status equals the requested status and it
     * matches the member filter (see {@link #membersMatch}).
     *
     * @param in the user, board, status, and members to filter by
     */
    @Override
    public void execute(FilterTasksInputData in) {
        final Board board = dataAccess.getBoard(in.getBoardId());

        if (board == null) {
            presenter.prepareFailView("Board not found.");
            return;
        }

        if (!board.hasMember(in.getUsername())) {
            presenter.prepareFailView("Access denied: you are not a member of this board.");
            return;
        }

        final List<String> taskIds = new ArrayList<>();
        final List<String> taskNames = new ArrayList<>();

        for (Task task : board.getTasks()) {
            final boolean matchesStatus = task.getStatus() == in.getStatus();
            final boolean matchesMembers = membersMatch(task, in.getMembers());

            if (matchesStatus && matchesMembers) {
                taskIds.add(task.getId());
                taskNames.add(task.getName());
            }
        }

        presenter.prepareSuccessView(new FilterTasksOutputData(taskIds, taskNames));
    }

    /**
     * Checks whether a task passes the member filter.
     *
     * @param task    the task to check
     * @param members usernames to filter by; an empty list means no member filter
     * @return true if the list is empty or the task is assigned to any listed member
     */
    private boolean membersMatch(Task task, List<String> members) {
        if (members.isEmpty()) {
            return true;
        }
        for (String member : members) {
            if (task.isAssignedTo(member)) {
                return true;
            }
        }
        return false;
    }
}
