package use_case.create_board;

public class CreateBoardOutputData {
    private final String boardId;
    private final String title;

    public CreateBoardOutputData(String boardId, String title) {
        this.boardId = boardId;
        this.title = title;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }
}