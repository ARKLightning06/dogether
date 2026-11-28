package use_case.login;

/**
 * The Input Boundary for logging into Dogether.
 */

public interface LoginInputBoundary {

    /**
     * Executes the log in use case
     * @param loginInputData represents the log in input data
     */

    void execute(LoginInputData loginInputData);

}
