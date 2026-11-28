package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserFactoryTest {

    UserFactory userFactory = new UserFactory();

    @Test
    void userFactoryCreatesUser(){
        assertInstanceOf(User.class, userFactory.create("ehinomen", "12345"));
    }

    @Test
    void userMethodsWork(){
        assertEquals("ehinomen", userFactory.create("ehinomen", "12345").getUsername());
        assertEquals("12345", userFactory.create("ehinomen", "12345").getPassword());
    }

    @Test
    void userExceptionWork(){
        assertThrows(IllegalArgumentException.class, () -> {userFactory.create("", "12345");});

    }
}
