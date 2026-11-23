package view;

import entity.Permission;
import interface_adapter.create_board.CreateBoardController;
import interface_adapter.create_board.CreateBoardState;
import interface_adapter.create_board.CreateBoardViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class CreateBoardView extends JPanel implements PropertyChangeListener {
    private final CreateBoardViewModel createBoardViewModel;
    private final CreateBoardController createBoardController;

    private final JTextField titleInputField = new JTextField(20);
    private final JTextField inviteeUsernameField = new JTextField(15);
    private final JComboBox<Permission> permissionComboBox = new JComboBox<>(Permission.values());
    private final DefaultListModel<String> inviteeListModel = new DefaultListModel<>();
    private final Map<String, Permission> currentInvitees = new HashMap<>();

    private final JLabel errorLabel = new JLabel();
    private final JButton addInviteeButton = new JButton("Add Invitee");
    private final JButton createButton = new JButton("Create Board");

    private String currentUsername;

    public CreateBoardView(CreateBoardViewModel createBoardViewModel,
                           CreateBoardController createBoardController) {
        this.createBoardViewModel = createBoardViewModel;
        this.createBoardController = createBoardController;
        this.createBoardViewModel.addPropertyChangeListener(this);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel header = new JLabel("Create a New Board");
        header.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Board title input
        JPanel titlePanel = new JPanel();
        titlePanel.add(new JLabel("Board Title:"));
        titlePanel.add(titleInputField);

        // Invitee input
        JPanel invitePanel = new JPanel();
        invitePanel.add(new JLabel("Invite Username:"));
        invitePanel.add(inviteeUsernameField);
        invitePanel.add(permissionComboBox);
        invitePanel.add(addInviteeButton);

        // List of added invitees
        JList<String> inviteeDisplayList = new JList<>(inviteeListModel);
        JScrollPane listScrollPane = new JScrollPane(inviteeDisplayList);
        listScrollPane.setPreferredSize(new Dimension(300, 100));

        // Validation error display
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Submit button
        JPanel actionPanel = new JPanel();
        actionPanel.add(createButton);

        addInviteeButton.addActionListener(e -> {
            String username = inviteeUsernameField.getText().trim();
            Permission permission = (Permission) permissionComboBox.getSelectedItem();
            if (!username.isEmpty() && permission != null) {
                currentInvitees.put(username, permission);
                refreshInviteeList();
                inviteeUsernameField.setText("");
            }
        });

        createButton.addActionListener(e -> {
            String title = titleInputField.getText();
            createBoardController.execute(
                    currentUsername,
                    title,
                    new HashMap<>(currentInvitees)
            );
        });

        add(header);
        add(titlePanel);
        add(invitePanel);
        add(listScrollPane);
        add(errorLabel);
        add(actionPanel);
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    private void refreshInviteeList() {
        inviteeListModel.clear();
        for (Map.Entry<String, Permission> entry : currentInvitees.entrySet()) {
            inviteeListModel.addElement(entry.getKey() + " (" + entry.getValue() + ")");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CreateBoardState state = (CreateBoardState) evt.getNewValue();
        if (state.getError() != null) {
            errorLabel.setText(state.getError());
        } else {
            errorLabel.setText("");
            titleInputField.setText(state.getTitle());
            currentInvitees.clear();
            currentInvitees.putAll(state.getInvitees());
            refreshInviteeList();
        }
    }
}