package interface_adapter.board;

import interface_adapter.ViewModel;
import java.awt.Point;
import java.util.List;

/**
 * Shared view model for the board screen.
 * Setters only update the state. Presenters call firePropertyChanged()
 * once after all updates so the view re-renders a single time.
 */
public class BoardViewModel extends ViewModel<BoardState> {
    public BoardViewModel() {
        super("board");
        setState(new BoardState());
    }

    public void setTaskIds(List<String> taskIds) { getState().setTaskIds(taskIds); }
    public void setTaskNames(List<String> taskNames) { getState().setTaskNames(taskNames); }
    public void setCompletedCount(int count) { getState().setCompletedCount(count); }
    public void setTotalCount(int count) { getState().setTotalCount(count); }
    public void setBoardId(String boardId) { getState().setBoardId(boardId); }
    public void setTitle(String title) { getState().setTitle(title); }
    public void setPlacementIds(List<String> placementIds) { getState().setPlacementIds(placementIds); }
    public void setImageUrls(List<String> imageUrls) { getState().setImageUrls(imageUrls); }
    public void setPositions(List<Point> positions) { getState().setPositions(positions); }
    public void setError(String error) { getState().setError(error); }
    public void setCompletionLabel(String label) { getState().setCompletionLabel(label); }
}
