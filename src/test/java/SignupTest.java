import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.signup.*;

import static org.junit.jupiter.api.Assertions.*;


public class SignupTest {

    private FakeDataAccess fakeDataAccess;
    private FakePresenter fakePresenter;
    private SignupInteractor signupInteractor;

    @BeforeEach
    void setUp() {
        fakeDataAccess = new FakeDataAccess();
        fakePresenter = new FakePresenter();
        signupInteractor = new SignupInteractor(fakeDataAccess, fakePresenter, new UserFactory());
    }

    static class FakeDataAccess implements SignupDataAccessInterface {
        boolean userExists = false;
        boolean saveCalled = false;
        User savedUser = null;

        public boolean existsByUsername(String name) {
            return userExists;
        }

        public void save(User user) {
            saveCalled = true;
            savedUser = user;
        }
    }

    static class FakePresenter implements SignupOutputBoundary {
        boolean successCalled = false;
        String errorMessage = null;
        SignupOutputData successData = null;
        boolean switchedView = false;

        public void prepareSuccessView(SignupOutputData data) {
            successCalled = true;
            successData = data;
        }
        public void prepareFailureView(String error) { errorMessage = error; }

        public void switchToLoginView() {
            switchedView = true;
        }
    }

    @Test
    void successfulSignup(){
        fakeDataAccess.userExists = false;

        signupInteractor.execute(new SignupInputData("ehinomen", "12345","12345"));

        assertTrue(fakePresenter.successCalled);
        assertTrue(fakeDataAccess.saveCalled);
        assertEquals("ehinomen", fakeDataAccess.savedUser.getUsername());
    }

    @Test
    void userAlreadyExists() {
        fakeDataAccess.userExists = true;

        signupInteractor.execute(new SignupInputData("Paul", "pw123", "pw123"));

        assertEquals("Username already exists.", fakePresenter.errorMessage);
        assertFalse(fakeDataAccess.saveCalled);
    }

    @Test
    void passwordsDontMatch() {
        signupInteractor.execute(new SignupInputData("Paul", "pw123", "different"));

        assertEquals("Passwords do not match.", fakePresenter.errorMessage);
        assertFalse(fakeDataAccess.saveCalled);
    }

    @Test
    void passwordIsEmpty() {
        signupInteractor.execute(new SignupInputData("Paul", "", ""));

        assertEquals("Password cannot be empty.", fakePresenter.errorMessage);
        assertFalse(fakeDataAccess.saveCalled);
    }

    @Test
    void usernameIsEmpty() {
        signupInteractor.execute(new SignupInputData("", "pw123", "different"));

        assertEquals("Username cannot be empty.", fakePresenter.errorMessage);
        assertFalse(fakeDataAccess.saveCalled);
    }

    @Test
    void switchesToLoginView(){
        signupInteractor. switchToLoginView();
        assertTrue(fakePresenter.switchedView);
    }
}
