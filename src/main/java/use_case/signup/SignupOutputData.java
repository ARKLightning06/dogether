package use_case.signup;

/**
 * The Output Data for signing up on Dogether.
 */

public class SignupOutputData {

    private final String username;

    public SignupOutputData(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
