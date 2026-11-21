package use_case.filter_tasks_by_status;

import entity.Board;
import entity.Task;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for the FilterTasks use case.
 * Filters a board's tasks by status and optionally by assigned members.
 */
public class FilterTasksInteractor implements FilterTasksInputBoundary{
    private final FilterTasksOutputBoundary presenter;
    private final FilterTasksDataAccessInterface dataAccess;

    public FilterTasksInteractor(FilterTasksOutputBoundary presenter, FilterTasksDataAccessInterface dataAccess){
        this.presenter = presenter;
        this.dataAccess = dataAccess;

    }
    /**
     * Filters tasks on the given board by status and assigned members.
     * If no members are specified, all tasks matching the status are returned.
     * @param in the input data
     */
    @Override
    public void execute(FilterTasksInputData in){
        Board board = dataAccess.getBoard(in.getBoardId());
        if (board == null){
            presenter.prepareFailView("Board not found.");
            return;
        }
        List<String> taskIds = new ArrayList<>();
        List<String> taskNames = new ArrayList<>();
        for (Task task: board.getTasks()){
            boolean matchesStatus = task.getStatus()==in.getStatus();

            boolean matchesMembers;
            if (in.getMembers().isEmpty()) {
                matchesMembers = true;
            }else{
                matchesMembers = false;
                for (String member : in.getMembers()) {
                    if (task.isAssignedTo(member)) {
                        matchesMembers = true;
                        break;
                    }
                }
            }
            if (matchesStatus && matchesMembers) {
                taskIds.add(task.getId());
                taskNames.add(task.getName());
            }

        }
        presenter.prepareSuccessView(new FilterTasksOutputData(taskIds, taskNames));
    }


}
