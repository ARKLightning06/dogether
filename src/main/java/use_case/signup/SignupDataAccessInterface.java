package use_case.signup;

import entity.User;

/**
 * The Data Access Interface for signing up.
 */

public interface SignupDataAccessInterface {

    /**
     * Checks if the username already exists
     * @param username is the user being searched for
     * Return true if it exists; false otherwise
     */

    boolean existsByUsername(String username);

    /**
     * Saves the user to the database
     * @param user is the user to be saved
     */

    void save(User user);
}
