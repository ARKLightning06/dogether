package use_case.signup;

/**
 * The Output Boundary for signing up on Dogether.
 */
public interface SignupOutputBoundary {

    /**
     * Prepares the success view for signing up.
     * @param signupOutputData represents the signup output data
     */

    void prepareSuccessView(SignupOutputData signupOutputData);

    /**
     * Prepares the failure view for signing up.
     * @param errorMessage represents the failure message
     */

    void prepareFailView(String errorMessage);

    /**
     * Switches to log in view.
     */

    void switchToLoginView();
}
