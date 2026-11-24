package use_case.load_dashboard;

import entity.Board;
import java.util.ArrayList;
import java.util.List;

public class LoadDashboardInteractor implements LoadDashboardInputBoundary{
    private final LoadDashboardDataAccessInterface dataAccess;
    private final LoadDashboardOutputBoundary presenter;

    public LoadDashboardInteractor(LoadDashboardDataAccessInterface dataAccess, LoadDashboardOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(LoadDashboardInputData inputData) {
        List<Board> boards = dataAccess.getBoardsForUser(inputData.getUsername());

        if (boards == null) {
            presenter.prepareFailView("Couldn't load boards for " + inputData.getUsername() + ". Try again.");
            return;
        }
        List<String> boardIds = new ArrayList<>();
        List<String> boardTitles = new ArrayList<>();


        for (Board board : boards) {
            boardIds.add(board.getId());
            boardTitles.add(board.getTitle());
        }

        LoadDashboardOutputData outputData = new LoadDashboardOutputData(boardIds, boardTitles);
        presenter.prepareSuccessView(outputData);
    }
}

