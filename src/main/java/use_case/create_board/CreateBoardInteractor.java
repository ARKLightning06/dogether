package use_case.create_board;

import entity.Board;
import entity.BoardFactory;
import entity.Invitation;
import entity.Permission;

import java.util.Map;

public class CreateBoardInteractor implements CreateBoardInputBoundary {
    private final CreateBoardDataAccessInterface dataAccess;
    private final CreateBoardOutputBoundary presenter;
    private final BoardFactory factory;

    public CreateBoardInteractor(CreateBoardDataAccessInterface dataAccess,
                                 CreateBoardOutputBoundary presenter,
                                 BoardFactory factory) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.factory = factory;
    }

    @Override
    public void execute(CreateBoardInputData in) {
        String title = in.getTitle();
        String ownerUsername = in.getOwnerUsername();
        Map<String, Permission> invitees = in.getInvitees();

        if (title == null || title.trim().isEmpty()) {
            presenter.prepareFailView("Board title cannot be empty.");
            return;
        }

        if (invitees != null) {
            for (String inviteeUsername : invitees.keySet()) {
                if (inviteeUsername.equals(ownerUsername)) {
                    presenter.prepareFailView("You cannot invite yourself to the board.");
                    return;
                }
                if (!dataAccess.userExists(inviteeUsername)) {
                    presenter.prepareFailView("User does not exist: " + inviteeUsername);
                    return;
                }
            }
        }

        Board board = factory.create(title.trim(), ownerUsername);

        if (invitees != null) {
            for (Map.Entry<String, Permission> entry : invitees.entrySet()) {
                String inviteeUsername = entry.getKey();
                Permission permission = entry.getValue();

                board.addMember(inviteeUsername, permission);
                Invitation invitation = new Invitation(board.getId(), inviteeUsername, permission);
                dataAccess.saveInvitation(invitation);
            }
        }

        dataAccess.saveBoard(board);

        CreateBoardOutputData outputData = new CreateBoardOutputData(board.getId(), board.getTitle());
        presenter.prepareSuccessView(outputData);
    }
}