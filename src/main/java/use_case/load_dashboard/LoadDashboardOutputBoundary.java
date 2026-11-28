package use_case.load_dashboard;

public interface LoadDashboardOutputBoundary {
    void prepareSuccessView(LoadDashboardOutputData outputData);
    void prepareFailView(String errorMessage);
}
