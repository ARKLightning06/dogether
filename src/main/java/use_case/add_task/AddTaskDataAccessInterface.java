
package use_case.add_task;

import entity.Board;

/**
 * The data access interface for the Add Task use case.
 */
public interface AddTaskDataAccessInterface {

    /**
     * Retrieves a board by its unique identifier.
     *
     * @param boardId the ID of the board to retrieve
     * @return the board, or null if it does not exist
     */
    Board getBoard(String boardId);

    /**
     * Saves the updated board.
     *
     * @param board the board to save
     */
    void saveBoard(Board board);
}
