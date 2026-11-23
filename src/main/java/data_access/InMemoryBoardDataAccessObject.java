package data_access;

import entity.Board;
import use_case.add_sticker.AddStickerDataAccessInterface;
import use_case.filter_tasks_by_status.FilterTasksDataAccessInterface;
import use_case.view_completion_count.ViewCompletionCountDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps boards in memory. Used for tests and for running the app without Firebase.
 */
public class InMemoryBoardDataAccessObject implements AddStickerDataAccessInterface,
        FilterTasksDataAccessInterface, ViewCompletionCountDataAccessInterface {
    private final Map<String, Board> boards = new HashMap<>();

    @Override
    public Board getBoard(String boardId) {
        return boards.get(boardId);
    }

    @Override
    public void saveBoard(Board board) {
        boards.put(board.getId(), board);
    }
}
