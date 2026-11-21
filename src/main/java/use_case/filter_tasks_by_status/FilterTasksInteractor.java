package use_case.filter_tasks_by_status;

import entity.Board;
import entity.Task;
import java.util.ArrayList;
import java.util.List;

public class FilterTasksInteractor implements FilterTasksInputBoundary {
    private final FilterTasksOutputBoundary presenter;
    private final FilterTasksDataAccessInterface dataAccess;

    public FilterTasksInteractor(FilterTasksOutputBoundary presenter,
                                 FilterTasksDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

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
