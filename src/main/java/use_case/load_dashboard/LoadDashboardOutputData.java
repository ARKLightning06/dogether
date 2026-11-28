package use_case.load_dashboard;

import java.util.List;

public class LoadDashboardOutputData {
    private final List<String> boardIds;
    private final List<String> boardTitles;

    public LoadDashboardOutputData(List<String> boardIds, List<String> boardTitles ) {
        this.boardIds = boardIds;
        this.boardTitles = boardTitles;
    }

    public List<String> getBoardIds() {
        return boardIds; }

    public List<String> getBoardTitles() {
        return boardTitles; }
}
