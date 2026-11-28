package interface_adapter.edit_task;

import interface_adapter.ViewModel;

/**
 * View model for the task editor, shared between the Add Task and
 * Edit Task use cases.
 */
public class TaskEditorViewModel extends ViewModel<TaskEditorState> {
    public static final String VIEW_NAME = "task editor";

    /**
     * Constructs a task editor view model with an initial empty state.
     */
    public TaskEditorViewModel() {
        super(VIEW_NAME);
        setState(new TaskEditorState());
    }
}
