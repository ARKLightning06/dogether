package interface_adapter;

/**
 * Manages the currently active view in the application.
 */
public class ViewManagerModel extends ViewModel<String> {
    private String activeViewName;

    /**
     * Constructs the view manager model.
     */
    public ViewManagerModel() {
        super("view manager");
    }
}
