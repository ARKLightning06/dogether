package use_case.logout;

/**
 * The Output Boundary for logging out of Dogether.
 */

public interface LogoutOutputBoundary {

    /**
     * Prepares the success view.
     * @param logoutOutputData represents the output data
     */

    void prepareSuccessView(LogoutOutputData logoutOutputData);

}
