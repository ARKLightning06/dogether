package use_case.create_board;

import entity.Board;
import entity.Invitation;

public interface CreateBoardDataAccessInterface {
    boolean userExists(String username);
    void saveBoard(Board board);
    void saveInvitation(Invitation invitation);
}