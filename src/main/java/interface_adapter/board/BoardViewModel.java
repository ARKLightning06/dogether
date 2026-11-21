package interface_adapter.board;

import interface_adapter.ViewModel;
import java.awt.Point;
import java.util.List;

public class BoardViewModel extends ViewModel<BoardState> {
    public BoardViewModel() {
        super("board");
        setState(new BoardState());
    }

    public void setTaskIds(List<String> taskIds) { getState().setTaskIds(taskIds); firePropertyChanged(); }
    public void setTaskNames(List<String> taskNames) { getState().setTaskNames(taskNames); firePropertyChanged(); }
    public void setCompletedCount(int count) { getState().setCompletedCount(count); firePropertyChanged(); }
    public void setTotalCount(int count) { getState().setTotalCount(count); firePropertyChanged(); }
    public void setBoardId(String boardId) { getState().setBoardId(boardId); firePropertyChanged(); }
    public void setTitle(String title) { getState().setTitle(title); firePropertyChanged(); }
    public void setPlacementIds(List<String> placementIds) { getState().setPlacementIds(placementIds); firePropertyChanged(); }
    public void setImageUrls(List<String> imageUrls) { getState().setImageUrls(imageUrls); firePropertyChanged(); }
    public void setPositions(List<Point> positions) { getState().setPositions(positions); firePropertyChanged(); }
    public void setError(String error) { getState().setError(error); firePropertyChanged(); }
}
