package use_case.logout;

/**
 * Represents the Output Data for logging out.
 */
public class LogoutOutputData {

    private final String username;

    public LogoutOutputData(String username){
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
