package interface_adapter.add_sticker;

import interface_adapter.board.BoardViewModel;
import use_case.add_sticker.AddStickerOutputBoundary;
import use_case.add_sticker.AddStickerOutputData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Puts a newly saved sticker (or an error) into the shared board view model.
 */
public class AddStickerPresenter implements AddStickerOutputBoundary {
    private final BoardViewModel viewModel;

    public AddStickerPresenter(BoardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(AddStickerOutputData outputData) {
        final List<String> placementIds = new ArrayList<>(viewModel.getState().getPlacementIds());
        final List<String> imageUrls = new ArrayList<>(viewModel.getState().getImageUrls());
        final List<Point> positions = new ArrayList<>(viewModel.getState().getPositions());
        placementIds.add(outputData.getPlacementId());
        imageUrls.add(outputData.getImageUrl());
        positions.add(new Point(outputData.getX(), outputData.getY()));

        viewModel.setPlacementIds(placementIds);
        viewModel.setImageUrls(imageUrls);
        viewModel.setPositions(positions);
        viewModel.setError("");
        viewModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setError(errorMessage);
        viewModel.firePropertyChanged();
    }
}
