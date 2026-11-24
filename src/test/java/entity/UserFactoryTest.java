package entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserFactoryTest {

    UserFactory userFactory = new UserFactory();

    @Test
    void UserFactoryCreatesUser(){
        assertInstanceOf(User.class, userFactory.create("ehinomen", "12345"));
    }

    @Test
    void UserMethodsWork(){
        User user = userFactory.create("ehinomen", "12345");
        assertEquals("ehinomen", userFactory.create("ehinomen", "12345").getUsername());
        assertEquals("12345", userFactory.create("ehinomen", "12345").getPassword());
    }

    @Test
    void UserExceptionWork(){
        assertThrows(IllegalArgumentException.class, () -> {userFactory.create("", "12345");});

    }
}
