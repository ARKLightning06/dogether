package use_case.login;

/**
 * The Output Boundary for logging into Dogether.
 */

public interface LoginOutputBoundary {

    /**
     * Prepares the success view for logging in
     * @param loginOutputData represents the log in output data
     */

    void preparesSuccessView(LoginOutputData loginOutputData);

    /**
     * Prepares the failure view for logging in
     * @param errorMessage represents the failure message
     */

    void preparesFailureView(String errorMessage);

}
