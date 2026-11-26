package view;

import interface_adapter.add_sticker.AddStickerController;
import interface_adapter.board.BoardState;
import interface_adapter.board.BoardViewModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Supplier;

/**
 * A see-through layer that sits on top of the whiteboard and shows the board's stickers.
 * When a sticker is bought in the shop, it appears here at a default spot; the user drags it
 * and saves (Use Case 2, steps 3 to 5). Clicks on empty space pass through to the board below.
 */
public class StickerLayer extends JPanel implements PropertyChangeListener, StickerPickListener {
    private static final int STICKER_SIZE = 96;
    private static final int DEFAULT_X = 40;
    private static final int DEFAULT_Y = 40;
    private static final int TOAST_MILLIS = 3500;

    private final BoardViewModel viewModel;
    private final AddStickerController addStickerController;
    private final Supplier<String> currentUser;
    private final JLabel toast = new JLabel("", SwingConstants.CENTER);
    private final Timer toastTimer = new Timer(TOAST_MILLIS, event -> toast.setVisible(false));
    private final JPanel placingBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));

    private JLabel pendingSticker;
    private String pendingStickerId;
    private String pendingImageUrl;
    private int stickersShown;

    /**
     * Creates the layer.
     *
     * @param viewModel the shared board view model (stickers come from its state)
     * @param addStickerController saves a placed sticker
     * @param currentUser gives the logged-in username
     */
    public StickerLayer(BoardViewModel viewModel, AddStickerController addStickerController,
                        Supplier<String> currentUser) {
        super(null);
        this.viewModel = viewModel;
        this.addStickerController = addStickerController;
        this.currentUser = currentUser;
        setOpaque(false);
        viewModel.addPropertyChangeListener(this);

        toast.setOpaque(true);
        toast.setFont(StickerStyle.body(java.awt.Font.PLAIN, 12f));
        toast.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        toast.setVisible(false);
        toastTimer.setRepeats(false);
        add(toast);

        final JButton save = smallButton("Save", StickerStyle.MINT);
        final JButton cancel = smallButton("Cancel", StickerStyle.WHITEBOARD);
        save.addActionListener(event -> savePending());
        cancel.addActionListener(event -> cancelPending());
        placingBar.setOpaque(false);
        placingBar.add(save);
        placingBar.add(cancel);
        placingBar.setVisible(false);
        add(placingBar);

        drawStickers(viewModel.getState());
    }

    /** Step 3: the bought sticker appears at a default spot, ready to drag. */
    @Override
    public void stickerPicked(String stickerId, String imageUrl) {
        removePending();
        pendingStickerId = stickerId;
        pendingImageUrl = imageUrl;
        pendingSticker = new JLabel("...", SwingConstants.CENTER);
        pendingSticker.setForeground(StickerStyle.MUTED);
        pendingSticker.setBorder(BorderFactory.createDashedBorder(StickerStyle.MUTED, 1.5f, 5, 3, true));
        pendingSticker.setBounds(DEFAULT_X, DEFAULT_Y, STICKER_SIZE, STICKER_SIZE);
        pendingSticker.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        makeDraggable(pendingSticker);
        add(pendingSticker, 0);
        StickerImages.load(imageUrl, STICKER_SIZE, icon -> {
            if (pendingSticker != null && imageUrl.equals(pendingImageUrl)) {
                pendingSticker.setText(icon == null ? "?" : "");
                pendingSticker.setIcon(icon);
            }
        });
        placingBar.setVisible(true);
        movePlacingBar();
        showToast("Drag your sticker where you want it, then save.", false);
        repaint();
    }

    /** Step 5: save where it was dropped (extension 4a: the default spot if it wasn't moved). */
    private void savePending() {
        if (pendingSticker == null) {
            return;
        }
        final BoardState state = viewModel.getState();
        final int x = pendingSticker.getX();
        final int y = pendingSticker.getY();
        final String stickerId = pendingStickerId;
        final String imageUrl = pendingImageUrl;
        final String username = currentUser.get();
        showToast("Saving...", false);
        new Thread(() -> addStickerController.execute(state.getBoardId(), username, stickerId, "",
                imageUrl, x, y)).start();
    }

    private void cancelPending() {
        removePending();
        showToast("Sticker not added.", false);
    }

    private void removePending() {
        if (pendingSticker != null) {
            remove(pendingSticker);
            pendingSticker = null;
        }
        pendingStickerId = null;
        pendingImageUrl = null;
        placingBar.setVisible(false);
        repaint();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final BoardState state = (BoardState) evt.getNewValue();
        SwingUtilities.invokeLater(() -> showState(state));
    }

    private void showState(BoardState state) {
        final int before = stickersShown;
        drawStickers(state);
        if (pendingSticker == null) {
            return;
        }
        final String error = state.getError();
        if (error != null && !error.isEmpty()) {
            // Extensions 2a and 5a: the sticker isn't kept
            removePending();
            showToast(error, true);
        } else if (stickersShown > before) {
            removePending();
            showToast("Sticker saved.", false);
        }
    }

    private void drawStickers(BoardState state) {
        for (java.awt.Component component : getComponents()) {
            if (component instanceof SavedSticker) {
                remove(component);
            }
        }
        final List<String> urls = state.getImageUrls();
        final List<Point> positions = state.getPositions();
        final int count = Math.min(urls.size(), positions.size());
        for (int i = 0; i < count; i++) {
            final SavedSticker label = new SavedSticker();
            label.setBounds(positions.get(i).x, positions.get(i).y, STICKER_SIZE, STICKER_SIZE);
            add(label);
            StickerImages.load(urls.get(i), STICKER_SIZE, icon -> {
                label.setText(icon == null ? "?" : "");
                label.setIcon(icon);
            });
        }
        stickersShown = count;
        // Keep the new sticker, its buttons and the message above saved stickers
        if (pendingSticker != null) {
            setComponentZOrder(pendingSticker, 0);
        }
        setComponentZOrder(placingBar, 0);
        setComponentZOrder(toast, 0);
        revalidate();
        repaint();
    }

    /** Step 4: lets the user move the new sticker around the board. */
    private void makeDraggable(JLabel label) {
        final Point grabOffset = new Point();
        final MouseAdapter dragger = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                grabOffset.setLocation(event.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                final Point inLayer = SwingUtilities.convertPoint(label, event.getPoint(), StickerLayer.this);
                label.setLocation(clamp(inLayer.x - grabOffset.x, getWidth() - label.getWidth()),
                        clamp(inLayer.y - grabOffset.y, getHeight() - label.getHeight()));
                movePlacingBar();
                repaint();
            }
        };
        label.addMouseListener(dragger);
        label.addMouseMotionListener(dragger);
    }

    private void movePlacingBar() {
        if (pendingSticker == null) {
            return;
        }
        final Dimension size = placingBar.getPreferredSize();
        placingBar.setBounds(pendingSticker.getX(), pendingSticker.getY() + STICKER_SIZE + 4,
                size.width, size.height);
    }

    private void showToast(String text, boolean isError) {
        toast.setText(text);
        if (isError) {
            toast.setBackground(StickerStyle.PINK);
            toast.setForeground(StickerStyle.ERROR);
        } else {
            toast.setBackground(StickerStyle.BUTTER);
            toast.setForeground(StickerStyle.TEXT);
        }
        final Dimension size = toast.getPreferredSize();
        toast.setBounds(12, Math.max(12, getHeight() - size.height - 12), size.width, size.height);
        toast.setVisible(true);
        setComponentZOrder(toast, 0);
        toastTimer.restart();
        repaint();
    }

    private static int clamp(int value, int max) {
        return Math.max(0, Math.min(value, Math.max(0, max)));
    }

    private static JButton smallButton(String text, Color background) {
        final JButton button = new JButton(text);
        button.setFont(StickerStyle.body(java.awt.Font.PLAIN, 12f));
        button.setForeground(StickerStyle.TEXT);
        button.setBackground(background);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StickerStyle.FRAME, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        return button;
    }

    /** A saved sticker on the board. */
    private static final class SavedSticker extends JLabel {
        private SavedSticker() {
            super("", SwingConstants.CENTER);
        }
    }
}
