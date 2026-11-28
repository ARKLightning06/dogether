package use_case.login;

import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {
    private FakeDataAccess fakeLoginDAO;
    private FakePresenter fakeLoginPresenter;
    private LoginInteractor loginInteractor;

    @BeforeEach
    void setUp(){
        fakeLoginDAO = new FakeDataAccess();
        fakeLoginPresenter = new FakePresenter();
        loginInteractor = new LoginInteractor(fakeLoginDAO, fakeLoginPresenter);
    }

    static class FakeDataAccess implements LoginDataAccessInterface {
        boolean userExists = false;
        User userToReturn = null;
        String currentUsername = null;

        public boolean existsByUsername(String username) {
            return userExists;
        }

        public User get(String username) {
            return userToReturn;
        }

        public void setCurrentUsername(String username) {
            currentUsername = username;
        }

    }

    static class FakePresenter implements LoginOutputBoundary {
        boolean successCalled = false;
        String errorMessage = null;

        public void prepareSuccessView(LoginOutputData loginOutputData) {
            successCalled =true;
        }

        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    @Test
    void successfulLogin(){
        fakeLoginDAO.userExists = true;
        fakeLoginDAO.userToReturn = new UserFactory().create("ehinomen", "12345");

        loginInteractor.execute(new LoginInputData("ehinomen", "12345"));

        assertTrue(fakeLoginPresenter.successCalled);
    }

    @Test
    void userDoesNotExist(){
        fakeLoginDAO.userExists = false;

        loginInteractor.execute(new LoginInputData("ehinomen", "12345"));

        assertEquals("User does not exist. Please navigate to sign up page.", fakeLoginPresenter.errorMessage);
        assertFalse(fakeLoginPresenter.successCalled);
    }

    @Test
    void incorrectPassword(){
        fakeLoginDAO.userExists = true;
        fakeLoginDAO.userToReturn = new UserFactory().create("ehinomen", "12345");

        loginInteractor.execute(new LoginInputData("ehinomen", "wrong"));

        assertEquals("Password is incorrect. Please try again.", fakeLoginPresenter.errorMessage);
        assertNull(fakeLoginDAO.currentUsername);
    }
}
