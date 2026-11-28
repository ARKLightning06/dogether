package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



class UserTest {

    @Test
    void constructor_validInput(){
        User user = new User("ehinomen", "12345");
        assertEquals("ehinomen", user.getUsername());
        assertEquals("12345", user.getPassword());

    }
    @Test
    void constructor_emptyUsername_throwsException(){
        assertThrows(IllegalArgumentException.class, () -> {
            new User("", "12345");
        });
    }

    @Test
    void constructor_emptyPassword_throwsException(){
        assertThrows(IllegalArgumentException.class, () -> {
            new User("ehinomen", "");
        });
    }

    @Test
    void constructor_nullUsername_throwsException(){
        assertThrows(IllegalArgumentException.class, () -> {
            new User(null, "12345");
        });
    }

    @Test
    void constructor_nullPassword_throwsException(){
        assertThrows(IllegalArgumentException.class, () -> {
            new User("ehinomen",null);
        });
    }
}
