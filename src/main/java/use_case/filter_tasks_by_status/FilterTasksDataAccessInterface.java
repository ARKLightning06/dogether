package use_case.filter_tasks_by_status;
import entity.Board;
/**
 * Data access interface for the FilterTasks use case.
 * Implemented by the DAO in the frameworks layer.
 */
public interface FilterTasksDataAccessInterface {
    /**
     * Retrieves a board by its ID.
     * @param boardId the ID of the board to retrieve
     * @return the Board, or null if not found
     */
    Board getBoard(String boardId);
}
