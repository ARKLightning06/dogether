
package interface_adapter.edit_task;

import interface_adapter.ViewManagerModel;
import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;
import java.util.ArrayList;
import java.util.List;
import use_case.edit_task.EditTaskOutputBoundary;
import use_case.edit_task.EditTaskOutputData;

/**
 * Presenter for the Edit Task use case.
 *
 * <p>Updates the task editor and board presentation states
 * after an edit succeeds or fails.
 */
public class EditTaskPresenter implements EditTaskOutputBoundary {
    private final EditTaskViewModel editTaskViewModel;
    private final BoardViewModel boardViewModel;
    private final ViewManagerModel viewManagerModel;

    /**
     * Constructs an Edit Task presenter.
     *
     * @param editTaskViewModel the view model for the task editor
     * @param boardViewModel the view model for the board
     * @param viewManagerModel the model responsible for navigation
     */
    public EditTaskPresenter(EditTaskViewModel editTaskViewModel,
                             BoardViewModel boardViewModel,
                             ViewManagerModel viewManagerModel) {
        this.editTaskViewModel = editTaskViewModel;
        this.boardViewModel = boardViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    /**
     * Updates the board and returns to the board view after a successful edit.
     *
     * @param outputData the information about the updated task
     */
    @Override
    public void prepareSuccessView(EditTaskOutputData outputData) {
        BoardState boardState = boardViewModel.getState();

        List<String> taskIds = boardState.getTaskIds();
        List<String> taskNames = new ArrayList<>(boardState.getTaskNames());

        int taskIndex = taskIds.indexOf(outputData.getTaskId());

        if (taskIndex >= 0) {
            taskNames.set(taskIndex, outputData.getName());
            boardState.setTaskNames(taskNames);
        }

        boardViewModel.firePropertyChanged();

        editTaskViewModel.setState(new EditTaskState());
        editTaskViewModel.firePropertyChanged();

        viewManagerModel.setState(boardViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    /**
     * Displays an error while preserving the task editor's input.
     *
     * @param error the message describing the editing failure
     */
    @Override
    public void prepareFailView(String error) {
        EditTaskState state = editTaskViewModel.getState();
        state.setError(error);

        editTaskViewModel.firePropertyChanged();
    }
}
