package view;

import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class BoardView extends JPanel implements PropertyChangeListener {
    private final BoardViewModel boardViewModel;

    private final JLabel titleLabel = new JLabel("Board Title");
    private final JLabel boardIdLabel = new JLabel("Board ID: ");
    private final JLabel errorLabel = new JLabel();

    public BoardView(BoardViewModel boardViewModel) {
        this.boardViewModel = boardViewModel;
        this.boardViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        boardIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(boardIdLabel);
        headerPanel.add(errorLabel);

        add(headerPanel, BorderLayout.NORTH);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        BoardState state = (BoardState) evt.getNewValue();
        if (state.getError() != null) {
            errorLabel.setText(state.getError());
        } else {
            errorLabel.setText("");
            titleLabel.setText(state.getTitle() != null ? state.getTitle() : "Board Title");
            boardIdLabel.setText("Board ID: " + (state.getBoardId() != null ? state.getBoardId() : ""));
        }
    }
}