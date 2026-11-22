package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;



class testUser {

    @Test
    void UserTest(){
        User user = new User("ehinomen", "12345");
        assertEquals("ehinomen", user.getUsername());
        assertEquals("12345", user.getPassword());

    }
}
