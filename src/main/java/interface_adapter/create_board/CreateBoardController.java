package interface_adapter.create_board;

import entity.Permission;
import use_case.create_board.CreateBoardInputBoundary;
import use_case.create_board.CreateBoardInputData;

import java.util.Map;

public class CreateBoardController {
    private final CreateBoardInputBoundary interactor;

    public CreateBoardController(CreateBoardInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String ownerUsername, String title, Map<String, Permission> invitees) {
        CreateBoardInputData inputData = new CreateBoardInputData(ownerUsername, title, invitees);
        interactor.execute(inputData);
    }
}