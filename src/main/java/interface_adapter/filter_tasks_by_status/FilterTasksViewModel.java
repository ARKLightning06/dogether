package interface_adapter.filter_tasks_by_status;

import java.util.List;

/**
 * ViewModel for the FilterTasks use case.
 * Holds the data the view needs to display.
 */
public class FilterTasksViewModel{
    private List<String> taskIds;
    private List<String> taskNames;
    private String error;

    public List<String> getTaskIds(){ return this.taskIds;}
    public void setTaskIds(List<String> taskIds) { this.taskIds = taskIds; }

    public List<String> getTaskNames(){ return this.taskNames; }
    public void setTaskNames(List<String> taskNames) { this.taskNames = taskNames; }

    public String getError(){return this.error;}
    public void setError(String error) { this.error = error; }

}