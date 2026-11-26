package view;

import interface_adapter.search_sticker.SearchStickerController;
import interface_adapter.search_sticker.SearchStickerState;
import interface_adapter.search_sticker.SearchStickerViewModel;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The sticker panel: a search bar and a grid of sticker tiles.
 * Images load in the background so the grid appears right away.
 */
public class StickerPanel extends JPanel implements PropertyChangeListener {
    // Colours from the team UI doc (see StickerStyle)
    private static final Color BACKGROUND = StickerStyle.BACKGROUND;
    private static final Color TILE = StickerStyle.WHITEBOARD;
    private static final Color TILE_HOVER = StickerStyle.PINK;
    private static final Color TILE_BORDER = StickerStyle.FRAME;
    private static final Color ACCENT = StickerStyle.PINK;
    private static final Color TEXT = StickerStyle.TEXT;
    private static final Color MUTED = StickerStyle.MUTED;
    private static final Color ERROR = StickerStyle.ERROR;

    private static final int COLUMNS = 4;
    private static final int TILE_SIZE = 112;
    private static final int IMAGE_SIZE = 92;
    private static final int LOADER_THREADS = 6;

    private final SearchStickerController controller;
    private final JTextField searchField = new HintTextField("Search stickers, like cat or star");
    private final JLabel message = new JLabel(" ");
    private final JPanel grid = new JPanel(new GridLayout(0, COLUMNS, 10, 10));
    private final ExecutorService imageLoader = Executors.newFixedThreadPool(LOADER_THREADS);
    private final Map<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private int searchNumber;
    private StickerPickListener pickListener;

    public StickerPanel(SearchStickerViewModel viewModel, SearchStickerController controller) {
        this.controller = controller;
        viewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout(0, 12));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        showEmptyState();
    }

    private JPanel buildHeader() {

        searchField.setFont(StickerStyle.body(Font.PLAIN, 14f));
        searchField.setForeground(TEXT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TILE_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        searchField.addActionListener(event -> search());

        final JButton searchButton = new JButton("Search");
        searchButton.setFocusPainted(false);
        searchButton.setFont(StickerStyle.body(Font.PLAIN, 13f));
        searchButton.setForeground(TEXT);
        searchButton.setBackground(ACCENT);
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(event -> search());

        final JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(searchButton, BorderLayout.EAST);

        message.setForeground(MUTED);
        message.setFont(StickerStyle.body(Font.PLAIN, 12f));

        final JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        header.add(searchRow, BorderLayout.CENTER);
        header.add(message, BorderLayout.SOUTH);
        return header;
    }

    private JScrollPane buildGrid() {
        grid.setOpaque(false);
        // Keeps tiles at the top instead of stretching to fill the space
        final JPanel holder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        holder.setOpaque(false);
        holder.add(grid);

        final JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel buildFooter() {
        final JLabel footer = new JLabel("Powered by GIPHY and Pixabay");
        footer.setForeground(MUTED);
        footer.setFont(StickerStyle.body(Font.PLAIN, 11f));
        return footer;
    }

    /** Tells the panel who to notify when a sticker is clicked. */
    public void setStickerPickListener(StickerPickListener listener) {
        this.pickListener = listener;
    }

    private void search() {
        message.setForeground(MUTED);
        message.setText("Searching...");
        final String query = searchField.getText();
        // Run off the UI thread so the window doesn't freeze during the HTTP call
        new Thread(() -> controller.execute(query)).start();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final SearchStickerState state = (SearchStickerState) evt.getNewValue();
        SwingUtilities.invokeLater(() -> showState(state));
    }

    private void showState(SearchStickerState state) {
        searchNumber++;
        grid.removeAll();
        if (state.getError() != null) {
            message.setForeground(ERROR);
            message.setText(state.getError());
        } else {
            message.setForeground(MUTED);
            message.setText(state.getImageUrls().size() + " stickers for \"" + state.getQuery() + "\"");
            showTiles(state.getStickerIds(), state.getImageUrls());
        }
        grid.revalidate();
        grid.repaint();
    }

    private void showTiles(List<String> stickerIds, List<String> imageUrls) {
        final int thisSearch = searchNumber;
        for (int i = 0; i < imageUrls.size(); i++) {
            final String url = imageUrls.get(i);
            final String stickerId = stickerIds.get(i);
            final StickerTile tile = new StickerTile();
            tile.setToolTipText("Buy for 1 star");
            tile.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (pickListener != null) {
                        pickListener.stickerPicked(stickerId, url);
                    }
                }
            });
            grid.add(tile);
            final ImageIcon cached = imageCache.get(url);
            if (cached != null) {
                tile.setImage(cached);
            } else {
                imageLoader.submit(() -> loadImage(url, tile, thisSearch));
            }
        }
    }

    /** Downloads and scales one image on a background thread, then shows it. */
    private void loadImage(String url, StickerTile tile, int forSearch) {
        try {
            // ImageIO reads the whole image before returning (first frame for GIFs)
            final BufferedImage original = ImageIO.read(URI.create(url).toURL());
            if (original == null) {
                SwingUtilities.invokeLater(tile::showFailed);
                return;
            }
            final ImageIcon scaled = scaleToFit(original);
            imageCache.put(url, scaled);
            SwingUtilities.invokeLater(() -> {
                // Skip if the user already started a newer search
                if (forSearch == searchNumber) {
                    tile.setImage(scaled);
                }
            });
        } catch (IOException | IllegalArgumentException ex) {
            SwingUtilities.invokeLater(tile::showFailed);
        }
    }

    /** Draws the image into a smaller copy that fits inside a tile. */
    private static ImageIcon scaleToFit(BufferedImage image) {
        final double scale = Math.min((double) IMAGE_SIZE / image.getWidth(),
                (double) IMAGE_SIZE / image.getHeight());
        final int newWidth = Math.max(1, (int) (image.getWidth() * scale));
        final int newHeight = Math.max(1, (int) (image.getHeight() * scale));
        final BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return new ImageIcon(scaled);
    }

    private void showEmptyState() {
        message.setText("Search for a sticker to decorate your board.");
    }

    /** One rounded square in the grid. Shows a soft placeholder until its image arrives. */
    private static final class StickerTile extends JPanel {
        private final JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        private boolean hovered;

        private StickerTile() {
            super(new BorderLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            imageLabel.setForeground(MUTED);
            add(imageLabel, BorderLayout.CENTER);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    hovered = false;
                    repaint();
                }
            });
        }

        private void setImage(ImageIcon icon) {
            imageLabel.setText("");
            imageLabel.setIcon(icon);
        }

        private void showFailed() {
            imageLabel.setText("?");
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            final Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (hovered) {
                g.setColor(TILE_HOVER);
            } else {
                g.setColor(TILE);
            }
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g.setColor(TILE_BORDER);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    /** A text field that shows grey hint text while it's empty. */
    private static final class HintTextField extends JTextField {
        private final String hint;

        private HintTextField(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (getText().isEmpty()) {
                final Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setColor(MUTED);
                g.setFont(getFont());
                final int baseline = (getHeight() + g.getFontMetrics().getAscent()
                        - g.getFontMetrics().getDescent()) / 2;
                g.drawString(hint, getInsets().left, baseline);
                g.dispose();
            }
        }
    }
}
