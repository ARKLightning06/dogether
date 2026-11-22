package entity;

public class User {
    private String username;
    private String password;

    public User( String username, String password){
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        } if (password.isEmpty()) {
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
