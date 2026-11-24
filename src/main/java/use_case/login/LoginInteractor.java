package use_case.login;

/**
 * The Interactor for logging in.
 */
public class LoginInteractor implements LoginInputBoundary{

    private final LoginOutputBoundary loginPresenter;
    private final LoginDataAccessInterface database;

    public LoginInteractor(LoginDataAccessInterface database, LoginOutputBoundary loginPresenter){
        this.database = database;
        this.loginPresenter = loginPresenter;
    }

    public void execute(LoginInputData loginInputData) {
        if (!database.existsByUsername(loginInputData.getUsername())){
            loginPresenter.preparesFailureView("User does not exist. Please navigate to sign up page.");
        }
        else if (!loginInputData.getPassword().equals(database.get(loginInputData.getUsername()).getPassword())){
            loginPresenter.preparesFailureView("Password is incorrect. Please try again.");
        }
        else{
            database.setCurrentUsername(loginInputData.getUsername());
            LoginOutputData loginOutputData = new LoginOutputData(loginInputData.getUsername());
            loginPresenter.preparesSuccessView(loginOutputData);
        }
    }

}
