package use_case.signup;

/**
 * The Input Boundary for signing up on Dogether.
 */

public interface SignupInputBoundary {

    /**
     * Executes the signup use case
     * @param signupInputData represents the input data
     */

    void execute( SignupInputData signupInputData);

    /**
     * Switches to log in view after signing up.
     */

    void switchToLoginView();
}
