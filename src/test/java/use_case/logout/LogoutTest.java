package use_case.logout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogoutTest {
    private FakeDataAccess fakeLogoutDAO;
    private FakePresenter fakeLogoutPresenter;
    private LogoutInteractor logoutInteractor;

    @BeforeEach
    void setUp() {
        fakeLogoutDAO = new FakeDataAccess();
        fakeLogoutPresenter = new FakePresenter();
        logoutInteractor = new LogoutInteractor(fakeLogoutDAO, fakeLogoutPresenter);
    }

    static class FakeDataAccess implements LogoutDataAccessInterface {
        String currentUsername = null;

        public String getCurrentUsername() { return currentUsername; }
        public void setCurrentUsername(String username) { currentUsername = username; }
    }

    static class FakePresenter implements LogoutOutputBoundary {
        boolean successCalled = false;
        LogoutOutputData successData = null;

        public void prepareSuccessView(LogoutOutputData data) {
            successCalled = true;
            successData = data;
        }
    }

    @Test
    void successfulLogout() {
        fakeLogoutDAO.currentUsername = "ehinomen";

        logoutInteractor.execute();

        assertTrue(fakeLogoutPresenter.successCalled);
        assertNull(fakeLogoutDAO.currentUsername);
    }

    @Test
    void outputContainsLoggedOutUsername() {
        fakeLogoutDAO.currentUsername = "ehinomen";

        logoutInteractor.execute();

        assertEquals("ehinomen", fakeLogoutPresenter.successData.getUsername());
    }
}