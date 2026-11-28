package interface_adapter.create_board;

import entity.Permission;
import java.util.HashMap;
import java.util.Map;

public class CreateBoardState {
    private String title = "";
    private Map<String, Permission> invitees = new HashMap<>();
    private String error = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Permission> getInvitees() {
        return invitees;
    }

    public void setInvitees(Map<String, Permission> invitees) {
        this.invitees = invitees;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}