package use_case.edit_task;

import entity.Board;

/**
 * The data access interface for the Edit Task use case.
 */
public interface EditTaskDataAccessInterface {

    /**
     * Retrieves the board containing the task to be edited.
     *
     * @param boardId the ID of the board to retrieve
     * @return the requested board
     */
    Board getBoard(String boardId);

    /**
     * Saves a board after one of its tasks has been updated.
     *
     * @param board the board containing the updated task
     */
    void saveBoard(Board board);
}
