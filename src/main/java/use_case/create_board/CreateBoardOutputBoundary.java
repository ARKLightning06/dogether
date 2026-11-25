package use_case.create_board;

public interface CreateBoardOutputBoundary {
    void prepareSuccessView(CreateBoardOutputData out);
    void prepareFailView(String error);
}