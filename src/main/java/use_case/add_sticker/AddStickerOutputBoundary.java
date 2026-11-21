package use_case.add_sticker;

public interface AddStickerOutputBoundary {
    void prepareSuccessView(AddStickerOutputData outputData);

    void prepareFailView(String errorMessage);
}