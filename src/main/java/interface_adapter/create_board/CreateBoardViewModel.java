package interface_adapter.create_board;

import interface_adapter.ViewModel;

public class CreateBoardViewModel extends ViewModel<CreateBoardState> {
    public static final String VIEW_NAME = "create board";

    public CreateBoardViewModel() {
        super(VIEW_NAME);
        setState(new CreateBoardState());
    }
}