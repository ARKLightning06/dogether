package use_case.logout;

/**
 * The Data Access Interface for logging out of Dogether
 */
public interface LogoutDataAccessInterface {

    /**
     * Sets the current username, the  current user of the application
     * @param username the current username
     */

    void setCurrentUsername(String username);

    /**
     * Returns the current username
     */

    String getCurrentUsername();
}
