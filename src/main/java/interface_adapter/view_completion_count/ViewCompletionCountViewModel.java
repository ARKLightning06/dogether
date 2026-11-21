package interface_adapter.view_completion_count;
/**
 * ViewModel for the ViewCompletionCount use case.
 * Holds the data the view needs to display.
 */
public class ViewCompletionCountViewModel {
    private int completed;
    private int total;
    private String error;

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getCompleted() {
        return this.completed;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return this.total;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return this.error;
    }

}