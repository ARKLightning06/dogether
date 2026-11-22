package entity;

/**
 * An entity that represents a user of Dogether. Users have a username and password.
 */

public class User {
    private String username;
    private String password;

    /**
     * Creates a user with a non-empty username and non-empty password.
     * @param username represents the user's username
     * @param password represent the user's password
     * @throws IllegalArgumentException if the username or password are empty
     */
    public User( String username, String password){
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
