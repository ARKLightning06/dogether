package use_case.filter_tasks_by_status;

import java.util.List;
/**
 * Output data for the FilterTasks use case.
 * Contains the IDs and names of tasks that matched the filter.
 */
public class FilterTasksOutputData {
    private final List<String> taskIds;
    private final List<String> taskNames;

    public FilterTasksOutputData(List<String> taskIds, List<String> taskNames) {
        this.taskIds = taskIds;
        this.taskNames = taskNames;
    }

    public List<String> getTaskIds() { return taskIds; }
    public List<String> getTaskNames() { return taskNames; }
}
