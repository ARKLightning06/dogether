package use_case.logout;

/**
 * Represents the Output Data for logging out.
 */
public class LogoutOutputData {

    private final String message;

    public LogoutOutputData(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
