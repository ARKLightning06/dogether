package use_case.login;

/**
 * The Output Boundary for logging into Dogether.
 */

public interface LoginOutputBoundary {

    /**
     * Prepares the success view for logging in
     * @param loginOutputData represents the log in output data
     */

    void prepareSuccessView(LoginOutputData loginOutputData);

    /**
     * Prepares the failure view for logging in
     * @param errorMessage represents the failure message
     */

    void prepareFailView(String errorMessage);

}
