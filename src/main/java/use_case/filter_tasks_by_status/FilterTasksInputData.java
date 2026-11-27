package use_case.filter_tasks_by_status;

import entity.TaskStatus;
import java.util.Collections;
import java.util.List;

/**
 * Input data for the FilterTasks use case.
 */
public class FilterTasksInputData {
    private final String username;
    private final String boardId;
    private final TaskStatus status;
    private final List<String> members;

    public FilterTasksInputData(String username, String boardId, TaskStatus status, List<String> members) {
        this.username = username;
        this.boardId = boardId;
        this.status = status;
        this.members = (members != null) ? members : Collections.emptyList();
    }

    public String getUsername() {
        return username;
    }

    public String getBoardId() {
        return boardId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public List<String> getMembers() {
        return members;
    }
}
