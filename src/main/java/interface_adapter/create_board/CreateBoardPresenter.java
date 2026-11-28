package interface_adapter.create_board;

import interface_adapter.ViewManagerModel;
import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;
import use_case.create_board.CreateBoardOutputBoundary;
import use_case.create_board.CreateBoardOutputData;

public class CreateBoardPresenter implements CreateBoardOutputBoundary {
    private final CreateBoardViewModel createBoardViewModel;
    private final BoardViewModel boardViewModel;
    private final ViewManagerModel viewManagerModel;

    public CreateBoardPresenter(CreateBoardViewModel createBoardViewModel,
                                BoardViewModel boardViewModel,
                                ViewManagerModel viewManagerModel) {
        this.createBoardViewModel = createBoardViewModel;
        this.boardViewModel = boardViewModel;
        this.viewManagerModel = viewManagerModel;
    }

    @Override
    public void prepareSuccessView(CreateBoardOutputData out) {
        // Update BoardViewModel with the new board's ID and title
        BoardState boardState = boardViewModel.getState();
        boardState.setBoardId(out.getBoardId());
        boardState.setTitle(out.getTitle());
        boardState.setError(null);
        boardViewModel.setState(boardState);
        boardViewModel.firePropertyChanged();

        // Reset CreateBoardViewModel state
        createBoardViewModel.setState(new CreateBoardState());
        createBoardViewModel.firePropertyChanged();

        // Switch the active view to BoardView
        viewManagerModel.setActiveViewName(boardViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        CreateBoardState state = createBoardViewModel.getState();
        state.setError(error);
        createBoardViewModel.setState(state);
        createBoardViewModel.firePropertyChanged();
    }
}