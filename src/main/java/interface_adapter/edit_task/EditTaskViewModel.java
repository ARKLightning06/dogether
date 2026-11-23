package interface_adapter.edit_task;

import interface_adapter.ViewModel;

/**
 * View model for the task editor, shared between the Add Task and
 * Edit Task use cases.
 */
public class EditTaskViewModel extends ViewModel<EditTaskState> {
    public static final String VIEW_NAME = "task editor";

    /**
     * Constructs a task editor view model with an initial empty state.
     */
    public EditTaskViewModel() {
        super(VIEW_NAME);
        setState(new EditTaskState());
    }
}
