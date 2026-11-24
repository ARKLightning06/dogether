package use_case.login;

import entity.User;

/**
 * The Data Access Interface for logging in.
 */

public interface LoginDataAccessInterface {

    /**
     * Checks if the given username exists.
     * @param username the username to look for
     * @return true if a user with the given username exists; false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Saves the user.
     * @param user the user to save
     */
    void save(User user);

    /**
     * Returns the user with the given username.
     * @param username the username to look up
     * @return the user with the given username
     */
    User get(String username);

    /**
     * Sets the current username of the user
     * @param username the current username
     */

    void setCurrentUsername(String username);

    /**
     * Returns the current username of the user
     */

    String getCurrentUsername();


}
