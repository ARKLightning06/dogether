package view;

import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;
import interface_adapter.view_completion_count.ViewCompletionCountController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A small rounded "pill" that shows how many of your tasks on this board you've
 * finished, as a thin progress bar and an "X of Y tasks completed" label.
 * Errors aren't shown here, because the error field in BoardState is shared
 * with other use cases.
 */
public class CompletionCountPanel extends JPanel implements PropertyChangeListener {
    private static final Color PILL = Color.WHITE;
    private static final Color BORDER = new Color(0xE6DFD4);
    private static final Color INK = new Color(0x2E2A26);
    private static final Color MUTED = new Color(0x6E675F);
    private static final Color TRACK = new Color(0xEFEAE2);
    private static final Color FILL = new Color(0x8FD3B8);
    private static final int ARC = 30;

    private final BoardViewModel viewModel;
    private final ViewCompletionCountController controller;
    private final JLabel countLabel = new JLabel("0 of 0 tasks completed");
    private final ProgressBar bar = new ProgressBar();
    private String currentUsername = "";

    /**
     * Creates the progress pill.
     *
     * @param viewModel the shared board view model to listen to
     * @param controller the controller that runs the ViewCompletionCount use case
     */
    public CompletionCountPanel(BoardViewModel viewModel, ViewCompletionCountController controller) {
        this.viewModel = viewModel;
        this.controller = controller;
        viewModel.addPropertyChangeListener(this);

        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));

        final JLabel title = new JLabel("Your progress");
        title.setForeground(MUTED);
        title.setFont(title.getFont().deriveFont(Font.PLAIN, 12f));
        countLabel.setForeground(INK);
        countLabel.setFont(countLabel.getFont().deriveFont(Font.PLAIN, 12f));

        add(title);
        add(bar);
        add(countLabel);
    }

    /**
     * Sets the user who is logged in, whose tasks are counted.
     *
     * @param currentUsername the logged-in user's username
     */
    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    /**
     * Asks for a fresh count for the current user and board. Call this when the
     * board opens and after a task is completed.
     */
    public void refresh() {
        controller.execute(currentUsername, viewModel.getState().getBoardId());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final BoardState state = viewModel.getState();
        bar.setProgress(state.getCompletedCount(), state.getTotalCount());
        final String label = state.getCompletionLabel();
        if (label == null || label.isEmpty()) {
            countLabel.setText(state.getCompletedCount() + " of " + state.getTotalCount() + " tasks completed");
        } else {
            countLabel.setText(label);
        }
    }

    JLabel getCountLabel() {
        return countLabel;
    }

    int getBarPercent() {
        return bar.percent();
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(PILL);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        g2.setColor(BORDER);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * A thin rounded progress bar in a pastel colour.
     */
    private static class ProgressBar extends JComponent {
        private static final int WIDTH = 90;
        private static final int HEIGHT = 6;
        private int completed;
        private int total;

        ProgressBar() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
        }

        void setProgress(int newCompleted, int newTotal) {
            this.completed = newCompleted;
            this.total = newTotal;
            repaint();
        }

        int percent() {
            if (total == 0) {
                return 0;
            }
            return completed * 100 / total;
        }

        @Override
        protected void paintComponent(Graphics g) {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            final int y = (getHeight() - HEIGHT) / 2;
            g2.setColor(TRACK);
            g2.fillRoundRect(0, y, getWidth(), HEIGHT, HEIGHT, HEIGHT);
            g2.setColor(FILL);
            g2.fillRoundRect(0, y, getWidth() * percent() / 100, HEIGHT, HEIGHT, HEIGHT);
            g2.dispose();
        }
    }
}
