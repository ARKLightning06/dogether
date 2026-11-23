package app;

import data_access.CachingStickerDataAccessObject;
import data_access.CombinedStickerDataAccessObject;
import data_access.GiphyStickerDataAccessObject;
import data_access.PixabayStickerDataAccessObject;
import interface_adapter.search_sticker.SearchStickerController;
import interface_adapter.search_sticker.SearchStickerPresenter;
import interface_adapter.search_sticker.SearchStickerViewModel;
import use_case.search_sticker.SearchStickerDataAccessInterface;
import use_case.search_sticker.SearchStickerInteractor;
import view.StickerPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.util.List;
/**
 * Builds the app from the outside in and opens the window.
 * Needs the GIPHY_API_KEY environment variable in the run configuration.
 */
public class Main {
    public static void main(String[] args) {
        // Frameworks and drivers: two sticker sources, merged and cached
        final SearchStickerDataAccessInterface stickerSearch = new CachingStickerDataAccessObject(
                new CombinedStickerDataAccessObject(List.of(
                        new PixabayStickerDataAccessObject(System.getenv("PIXABAY_API_KEY")),
                        new GiphyStickerDataAccessObject(System.getenv("GIPHY_API_KEY")))));
        // Interface adapters and use case, each given only what it depends on
        final SearchStickerViewModel viewModel = new SearchStickerViewModel();
        final SearchStickerPresenter presenter = new SearchStickerPresenter(viewModel);
        final SearchStickerInteractor interactor = new SearchStickerInteractor(stickerSearch, presenter);
        final SearchStickerController controller = new SearchStickerController(interactor);
        // View
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JFrame("DoGether");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new StickerPanel(viewModel, controller));
            frame.setSize(720, 560);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}