package entity;

public class Invitation {
    private final String boardId;
    private final String inviteeUsername;
    private final Permission permission;

    public Invitation(String boardId, String inviteeUsername, Permission permission) {
        this.boardId = boardId;
        this.inviteeUsername = inviteeUsername;
        this.permission = permission;
    }

    public String getBoardId() { return boardId; }
    public String getInviteeUsername() { return inviteeUsername; }
    public Permission getPermission() { return permission; }
}