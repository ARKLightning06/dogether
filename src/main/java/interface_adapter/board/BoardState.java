package interface_adapter.board;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

public class BoardState {
    private String boardId = "";
    private String title = "";
    private List<String> taskIds = Collections.emptyList();
    private List<String> taskNames = Collections.emptyList();
    private int completedCount;
    private int totalCount;
    private List<String> placementIds = Collections.emptyList();
    private List<String> imageUrls = Collections.emptyList();
    private List<Point> positions = Collections.emptyList();
    private String error = "";
    private String completionLabel = "";

    public String getBoardId() { return boardId; }
    public void setBoardId(String boardId) { this.boardId = boardId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getTaskIds() { return taskIds; }
    public void setTaskIds(List<String> taskIds) { this.taskIds = taskIds; }
    public List<String> getTaskNames() { return taskNames; }
    public void setTaskNames(List<String> taskNames) { this.taskNames = taskNames; }
    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public List<String> getPlacementIds() { return placementIds; }
    public void setPlacementIds(List<String> placementIds) { this.placementIds = placementIds; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public List<Point> getPositions() { return positions; }
    public void setPositions(List<Point> positions) { this.positions = positions; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getCompletionLabel() { return completionLabel; }
    public void setCompletionLabel(String completionLabel) { this.completionLabel = completionLabel; }
}
