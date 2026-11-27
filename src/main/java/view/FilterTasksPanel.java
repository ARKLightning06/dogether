package view;

import entity.TaskStatus;
import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;
import interface_adapter.filter_tasks_by_status.FilterTasksController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 * Lets the user filter the board's tasks by status, and optionally show only
 * their own tasks. Every change calls FilterTasksController right away, and the
 * list redraws when BoardViewModel fires.
 */
public class FilterTasksPanel extends JPanel implements PropertyChangeListener {
    private static final Color PAPER = Color.WHITE;
    private static final Color INK = new Color(0x2E2A26);
    private static final Color MUTED = new Color(0x6E675F);
    private static final Color LINE = new Color(0xEFEAE2);
    private static final Color HIGHLIGHT = new Color(0xDEDAFB);
    private static final Color ERROR = new Color(0xC0392B);

    private final BoardViewModel viewModel;
    private final FilterTasksController controller;
    private final JComboBox<TaskStatus> statusBox = new JComboBox<>(TaskStatus.values());
    private final JCheckBox onlyMineBox = new JCheckBox("Only my tasks");
    private final DefaultListModel<String> results = new DefaultListModel<>();
    private final JLabel countLabel = new JLabel(" ");
    private final JLabel errorLabel = new JLabel(" ");
    private String currentUsername = "";

    /**
     * Creates the filter panel.
     *
     * @param viewModel the shared board view model to listen to
     * @param controller the controller that runs the FilterTasks use case
     */
    public FilterTasksPanel(BoardViewModel viewModel, FilterTasksController controller) {
        this.viewModel = viewModel;
        this.controller = controller;
        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(0, 8));
        setBackground(PAPER);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        add(buildControls(), BorderLayout.NORTH);
        add(buildResultsList(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        statusBox.addActionListener(e -> applyFilter());
        onlyMineBox.addActionListener(e -> applyFilter());
    }

    /**
     * Sets the user who is logged in, so the filter knows whose board access to check
     * and whose tasks "Only my tasks" means.
     *
     * @param currentUsername the logged-in user's username
     */
    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    /**
     * Runs the filter with the current status and checkbox choices.
     */
    public void applyFilter() {
        final TaskStatus status = (TaskStatus) statusBox.getSelectedItem();
        final List<String> members;
        if (onlyMineBox.isSelected()) {
            members = List.of(currentUsername);
        } else {
            members = Collections.emptyList();
        }
        controller.execute(currentUsername, viewModel.getState().getBoardId(), status, members);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final BoardState state = viewModel.getState();
        final String error = state.getError();
        if (error != null && !error.isEmpty()) {
            errorLabel.setText(error);
            return;
        }
        errorLabel.setText(" ");
        results.clear();
        for (String name : state.getTaskNames()) {
            results.addElement(name);
        }
        final int count = results.size();
        if (count == 1) {
            countLabel.setText("1 task");
        } else {
            countLabel.setText(count + " tasks");
        }
    }

    JComboBox<TaskStatus> getStatusBox() {
        return statusBox;
    }

    JCheckBox getOnlyMineBox() {
        return onlyMineBox;
    }

    DefaultListModel<String> getResults() {
        return results;
    }

    JLabel getErrorLabel() {
        return errorLabel;
    }

    private JPanel buildControls() {
        final JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        controls.setOpaque(false);

        final JLabel title = new HighlightLabel("Filter tasks");
        title.setFont(new Font(Font.SERIF, Font.ITALIC | Font.BOLD, 20));
        title.setForeground(INK);

        statusBox.setRenderer(new StatusRenderer());
        statusBox.setSelectedItem(TaskStatus.TO_DO);
        onlyMineBox.setOpaque(false);
        onlyMineBox.setForeground(MUTED);
        onlyMineBox.setFont(onlyMineBox.getFont().deriveFont(Font.PLAIN, 13f));
        statusBox.setFont(statusBox.getFont().deriveFont(Font.PLAIN, 13f));

        controls.add(title);
        controls.add(statusBox);
        controls.add(onlyMineBox);
        return controls;
    }

    private JList<String> buildResultsList() {
        final JList<String> list = new JList<>(results);
        list.setCellRenderer(new TaskRowRenderer());
        list.setBackground(PAPER);
        return list;
    }

    private JPanel buildFooter() {
        final JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        countLabel.setForeground(MUTED);
        countLabel.setFont(countLabel.getFont().deriveFont(Font.PLAIN, 12f));
        errorLabel.setForeground(ERROR);
        errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 12f));
        footer.add(countLabel, BorderLayout.WEST);
        footer.add(errorLabel, BorderLayout.EAST);
        return footer;
    }

    /**
     * Turns TO_DO into "To do" and so on, for the dropdown.
     *
     * @param status the status to show
     * @return a readable name for the status
     */
    static String readable(TaskStatus status) {
        final String lower = status.name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    /**
     * Shows statuses in the dropdown as "To do", "In progress", "Done".
     */
    private static class StatusRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            final Object text;
            if (value instanceof TaskStatus) {
                text = readable((TaskStatus) value);
            } else {
                text = value;
            }
            return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
        }
    }

    /**
     * Draws each task as a row with a thin line underneath, no boxes.
     */
    private static class TaskRowRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            final JLabel row = (JLabel) super.getListCellRendererComponent(
                    list, "•  " + value, index, isSelected, cellHasFocus);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                    BorderFactory.createEmptyBorder(8, 6, 7, 6)));
            row.setForeground(INK);
            row.setFont(list.getFont().deriveFont(Font.PLAIN, 14f));
            if (isSelected) {
                row.setBackground(HIGHLIGHT);
            } else {
                row.setBackground(PAPER);
            }
            return row;
        }
    }

    /**
     * A label with a pastel highlighter stroke behind the lower half of the text.
     */
    private static class HighlightLabel extends JLabel {
        HighlightLabel(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(HIGHLIGHT);
            final int textWidth = g2.getFontMetrics(getFont()).stringWidth(getText());
            final int top = getHeight() / 2;
            g2.fillRoundRect(0, top, textWidth + 6, getHeight() - top - 2, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
