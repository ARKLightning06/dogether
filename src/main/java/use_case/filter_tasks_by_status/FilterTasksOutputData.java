package use_case.filter_tasks_by_status;

import java.util.List;

/**
 * Output data for the FilterTasks use case.
 * Contains the IDs and names of tasks that matched the filter.
 * The two lists are parallel: {@code taskNames.get(i)} is the name of
 * the task with ID {@code taskIds.get(i)}.
 */
public class FilterTasksOutputData {
    private final List<String> taskIds;
    private final List<String> taskNames;

    /**
     * Creates the output data.
     *
     * @param taskIds   IDs of the matching tasks, in board order
     * @param taskNames names of the matching tasks, in the same order as taskIds
     */
    public FilterTasksOutputData(List<String> taskIds, List<String> taskNames) {
        this.taskIds = taskIds;
        this.taskNames = taskNames;
    }

    /**
     * Returns the IDs of the matching tasks.
     *
     * @return task IDs, in board order; empty if nothing matched
     */
    public List<String> getTaskIds() {
        return taskIds;
    }

    /**
     * Returns the names of the matching tasks.
     *
     * @return task names, in the same order as {@link #getTaskIds()}
     */
    public List<String> getTaskNames() {
        return taskNames;
    }
}

