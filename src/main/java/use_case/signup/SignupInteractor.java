package use_case.signup;

import entity.User;
import entity.UserFactory;

/**
 * The Signup Use Case Interactor.
 */

public class SignupInteractor implements SignupInputBoundary{

    private final SignupDataAccessInterface database;
    private final SignupOutputBoundary signupPresenter;
    private final UserFactory userFactory;

    public SignupInteractor(SignupDataAccessInterface database, SignupOutputBoundary signupPresenter,
                            UserFactory userFactory){
        this.database = database;
        this.signupPresenter = signupPresenter;
        this.userFactory = userFactory;
    }

    public void execute(SignupInputData signupInputData){
        if (database.existsByUsername(signupInputData.getUsername())) {
            signupPresenter.prepareFailureView("Username already exists.");
        } else if ("".equals(signupInputData.getUsername())) {
            signupPresenter.prepareFailureView("Username cannot be empty.");
        } else if ("".equals(signupInputData.getPassword())) {
            signupPresenter.prepareFailureView("Password cannot be empty.");
        } else if (!signupInputData.getPassword().equals(signupInputData.getRepeatPassword())) {
            signupPresenter.prepareFailureView("Passwords do not match.");
        }
        else{
            final User user = userFactory.create(signupInputData.getUsername(), signupInputData.getPassword());
            database.save(user);
            final SignupOutputData signupOutputData = new SignupOutputData(user.getUsername());
            signupPresenter.prepareSuccessView(signupOutputData);

        }
    }

    public void switchToLoginView(){signupPresenter.switchToLoginView();
    }
}
