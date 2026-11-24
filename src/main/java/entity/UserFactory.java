package entity;

/**
 * A class responsible for creating instances of the User entity
 */

public class UserFactory {

    public User create(String username, String password){
        return new User(username, password);
    }
}
