package use_case.load_dashboard;

import java.util.List;
import entity.Board;

public interface LoadDashboardDataAccessInterface {
    List<Board> getBoardsForUser(String username);
}
