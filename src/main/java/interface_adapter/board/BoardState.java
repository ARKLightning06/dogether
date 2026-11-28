package interface_adapter.board;

import entity.Permission;
import entity.TaskStatus;
import java.awt.Point;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * State shown on the board screen. Presenters fill it in through
 * BoardViewModel, and the board view reads it when it redraws.
 */
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
    /** The list each task is in (parallel to taskIds). */
    private List<String> taskListNames = Collections.emptyList();
    /** Each task's deadline, or null if it has none (parallel to taskIds). */
    private List<LocalDate> taskDeadlines = Collections.emptyList();
    /** Each task's status (parallel to taskIds). */
    private List<TaskStatus> taskStatuses = Collections.emptyList();
    /** The usernames assigned to each task (parallel to taskIds). */
    private List<List<String>> taskAssignees = Collections.emptyList();
    /** Board members and whether they can edit or only view. */
    private Map<String, Permission> members = Collections.emptyMap();
    /** The tasks the current filter shows. The rest are hidden, not removed. */
    private List<String> visibleTaskIds = Collections.emptyList();
    /** How many tasks on the whole board are done. */
    private int boardCompletedCount;
    /** How many tasks are on the whole board. */
    private int boardTotalCount;
    /** The board's shared stars, spent on stickers. */
    private int stars;
    /** Text like "+2 gold stars earned" for the current user. */
    private String starsEarnedLabel = "";

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }

    public List<String> getTaskNames() {
        return taskNames;
    }

    public void setTaskNames(List<String> taskNames) {
        this.taskNames = taskNames;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<String> getPlacementIds() {
        return placementIds;
    }

    public void setPlacementIds(List<String> placementIds) {
        this.placementIds = placementIds;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<Point> getPositions() {
        return positions;
    }

    public void setPositions(List<Point> positions) {
        this.positions = positions;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCompletionLabel() {
        return completionLabel;
    }

    public void setCompletionLabel(String completionLabel) {
        this.completionLabel = completionLabel;
    }

    public List<String> getTaskListNames() {
        return taskListNames;
    }

    public void setTaskListNames(List<String> taskListNames) {
        this.taskListNames = taskListNames;
    }

    public List<LocalDate> getTaskDeadlines() {
        return taskDeadlines;
    }

    public void setTaskDeadlines(List<LocalDate> taskDeadlines) {
        this.taskDeadlines = taskDeadlines;
    }

    public List<TaskStatus> getTaskStatuses() {
        return taskStatuses;
    }

    public void setTaskStatuses(List<TaskStatus> taskStatuses) {
        this.taskStatuses = taskStatuses;
    }

    public List<List<String>> getTaskAssignees() {
        return taskAssignees;
    }

    public void setTaskAssignees(List<List<String>> taskAssignees) {
        this.taskAssignees = taskAssignees;
    }

    public Map<String, Permission> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Permission> members) {
        this.members = members;
    }

    public List<String> getVisibleTaskIds() {
        return visibleTaskIds;
    }

    public void setVisibleTaskIds(List<String> visibleTaskIds) {
        this.visibleTaskIds = visibleTaskIds;
    }

    public int getBoardCompletedCount() {
        return boardCompletedCount;
    }

    public void setBoardCompletedCount(int boardCompletedCount) {
        this.boardCompletedCount = boardCompletedCount;
    }

    public int getBoardTotalCount() {
        return boardTotalCount;
    }

    public void setBoardTotalCount(int boardTotalCount) {
        this.boardTotalCount = boardTotalCount;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getStarsEarnedLabel() {
        return starsEarnedLabel;
    }

    public void setStarsEarnedLabel(String starsEarnedLabel) {
        this.starsEarnedLabel = starsEarnedLabel;
    }
}
