package use_case.login;

/**
 * The Output Data for logging in.
 */
public class LoginOutputData {

    private final String username;

    public LoginOutputData(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
