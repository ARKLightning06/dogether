package use_case.create_board;

import entity.Permission;
import java.util.Map;

public class CreateBoardInputData {
    private final String ownerUsername;
    private final String title;
    private final Map<String, Permission> invitees;

    public CreateBoardInputData(String ownerUsername, String title, Map<String, Permission> invitees) {
        this.ownerUsername = ownerUsername;
        this.title = title;
        this.invitees = invitees;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, Permission> getInvitees() {
        return invitees;
    }
}