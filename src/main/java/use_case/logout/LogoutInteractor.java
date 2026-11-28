package use_case.logout;

/**
 * The Output Boundary for logging out.
 */

public class LogoutInteractor implements LogoutInputBoundary{

    private final LogoutDataAccessInterface logoutDataAccess;
    private final LogoutOutputBoundary logoutPresenter;

    public LogoutInteractor( LogoutDataAccessInterface logoutDataAccess,LogoutOutputBoundary logoutPresenter){
        this.logoutDataAccess = logoutDataAccess;
        this.logoutPresenter = logoutPresenter;
    }

    public void execute(){
        final String username = logoutDataAccess.getCurrentUsername();
        logoutDataAccess.setCurrentUsername(null);
        final LogoutOutputData logoutOutputData = new LogoutOutputData(username);
        logoutPresenter.prepareSuccessView(logoutOutputData);
    }
}
