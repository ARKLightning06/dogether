package view;

import entity.TaskStatus;
import interface_adapter.add_task.AddTaskController;
import interface_adapter.edit_task.EditTaskController;
import interface_adapter.edit_task.EditTaskState;
import interface_adapter.edit_task.EditTaskViewModel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * The shared Swing view for adding and editing tasks.
 *
 * <p>The view displays the task editor state and delegates task
 * creation and editing to their respective controllers.
 */
public class EditTaskView extends JPanel implements PropertyChangeListener {
    private final EditTaskViewModel EditTaskViewModel;
    private final AddTaskController addTaskController;
    private final EditTaskController editTaskController;

    private final JTextField nameField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private final JTextField deadlineField = new JTextField();
    private final JTextField colourField = new JTextField();
    private final JTextField assigneesField = new JTextField();
    private final JComboBox<TaskStatus> statusSelector =
            new JComboBox<>(TaskStatus.values());

    private final JLabel titleLabel = new JLabel();
    private final JLabel errorLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("Status:");
    private final JButton saveButton = new JButton("Save Task");

    private String username;
    private String boardId;

    /**
     * Constructs the shared task editor view.
     *
     * @param EditTaskViewModel the view model for the editor
     * @param addTaskController the controller for creating tasks
     * @param editTaskController the controller for editing tasks
     */
    public EditTaskView(EditTaskViewModel EditTaskViewModel,
                          AddTaskController addTaskController,
                          EditTaskController editTaskController) {
        this.EditTaskViewModel = EditTaskViewModel;
        this.addTaskController = addTaskController;
        this.editTaskController = editTaskController;

        setLayout(new BorderLayout(10, 10));

        initializeComponents();

        EditTaskViewModel.addPropertyChangeListener(this);
        updateFields();
    }

    /**
     * Initializes and arranges the Swing components.
     */
    private void initializeComponents() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        formPanel.add(new JLabel("Task Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea));

        formPanel.add(new JLabel("Deadline (YYYY-MM-DD):"));
        formPanel.add(deadlineField);

        formPanel.add(new JLabel("Colour:"));
        formPanel.add(colourField);

        formPanel.add(new JLabel("Assignees (comma-separated):"));
        formPanel.add(assigneesField);

        formPanel.add(statusLabel);
        formPanel.add(statusSelector);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(errorLabel, BorderLayout.CENTER);
        bottomPanel.add(saveButton, BorderLayout.SOUTH);

        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(event -> saveTask());
    }

    /**
     * Sets the current user and selected board for task operations.
     *
     * @param username the logged-in user's username
     * @param boardId the ID of the currently selected board
     */
    public void setEditorContext(String username, String boardId) {
        this.username = username;
        this.boardId = boardId;
    }

    /**
     * Returns the name used to identify this view.
     *
     * @return the task editor view name
     */
    public String getViewName() {
        return EditTaskViewModel.getViewName();
    }

    /**
     * Executes the appropriate task use case using the form input.
     */
    private void saveTask() {
        if (username == null || username.isBlank()
                || boardId == null || boardId.isBlank()) {
            errorLabel.setText("No user or board selected.");
            return;
        }

        final LocalDate deadline;

        try {
            String deadlineText = deadlineField.getText().trim();
            deadline = deadlineText.isEmpty()
                    ? null : LocalDate.parse(deadlineText);
        } catch (DateTimeParseException exception) {
            errorLabel.setText("Invalid deadline. Use YYYY-MM-DD.");
            return;
        }

        List<String> assignees = parseAssignees();
        EditTaskState state = EditTaskViewModel.getState();

        state.setName(nameField.getText());
        state.setDescription(descriptionArea.getText());
        state.setDeadline(deadline);
        state.setColour(colourField.getText());
        state.setAssignees(assignees);
        state.setStatus((TaskStatus) statusSelector.getSelectedItem());
        state.setError("");

        errorLabel.setText("");

        try {
            if (isEditMode()) {
                editTaskController.execute(
                        username,
                        boardId,
                        state.getTaskId(),
                        state.getName(),
                        state.getDescription(),
                        state.getDeadline(),
                        state.getColour(),
                        state.getAssignees(),
                        state.getStatus());
            } else {
                addTaskController.execute(
                        username,
                        boardId,
                        state.getName(),
                        state.getDescription(),
                        state.getDeadline(),
                        state.getColour(),
                        state.getAssignees());
            }
        } catch (IllegalArgumentException exception) {
            state.setError(exception.getMessage());
            errorLabel.setText(state.getError());
        }
    }

    /**
     * Converts the comma-separated assignee field into usernames.
     *
     * @return the list of usernames entered by the user
     */
    private List<String> parseAssignees() {
        List<String> assignees = new ArrayList<>();
        String input = assigneesField.getText();

        if (input.isBlank()) {
            return assignees;
        }

        String[] usernames = input.split(",", -1);

        for (String username : usernames) {
            assignees.add(username.trim());
        }

        return assignees;
    }

    /**
     * Returns whether the editor is modifying an existing task.
     *
     * @return true if an existing task ID is present
     */
    private boolean isEditMode() {
        String taskId = EditTaskViewModel.getState().getTaskId();
        return taskId != null && !taskId.isBlank();
    }

    /**
     * Updates the Swing components to reflect the current editor state.
     */
    private void updateFields() {
        EditTaskState state = EditTaskViewModel.getState();

        if (state == null) {
            return;
        }

        nameField.setText(state.getName());
        descriptionArea.setText(state.getDescription());

        if (state.getDeadline() == null) {
            deadlineField.setText("");
        } else {
            deadlineField.setText(state.getDeadline().toString());
        }

        colourField.setText(state.getColour());
        assigneesField.setText(String.join(", ", state.getAssignees()));
        statusSelector.setSelectedItem(state.getStatus());

        boolean editing = isEditMode();

        titleLabel.setText(editing ? "Edit Task" : "Add Task");
        saveButton.setText(editing ? "Save Changes" : "Create Task");

        statusSelector.setVisible(editing);
        statusLabel.setVisible(editing);

        errorLabel.setText(state.getError());
    }

    /**
     * Updates the view when the task editor state changes.
     *
     * @param event the property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("state".equals(event.getPropertyName())) {
            updateFields();
        }
    }
}
