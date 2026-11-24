package use_case.load_dashboard;

public class LoadDashboardInputData {
    private final String username;

    //creates user's username
    public LoadDashboardInputData(String username) {
        this.username = username;
    }

    //return the username
    public String getUsername() {
        return username;
    }

}
