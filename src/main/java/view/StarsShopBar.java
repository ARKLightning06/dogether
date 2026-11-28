package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * The board's star count and the button that opens the sticker shop.
 * Meant to sit in the board's top bar.
 */
public class StarsShopBar extends JPanel {
    private final JLabel starsLabel = new JLabel();
    private final ShopDialog shop;

    /**
     * Creates the bar.
     *
     * @param shop the shop popup to open
     */
    public StarsShopBar(ShopDialog shop) {
        super(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        this.shop = shop;
        setOpaque(false);

        final JPanel pill = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            @Override
            protected void paintComponent(Graphics graphics) {
                final Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(StickerStyle.BUTTER);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        starsLabel.setFont(StickerStyle.body(Font.BOLD, 13f));
        starsLabel.setForeground(StickerStyle.TEXT);
        pill.add(starsLabel);

        final JButton shopButton = new JButton("Shop");
        shopButton.setFont(StickerStyle.body(Font.PLAIN, 13f));
        shopButton.setForeground(StickerStyle.TEXT);
        shopButton.setBackground(StickerStyle.PINK);
        shopButton.setOpaque(true);
        shopButton.setFocusPainted(false);
        shopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        shopButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StickerStyle.FRAME, 1, true),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)));
        shopButton.addActionListener(event -> shop.setVisible(true));

        add(pill);
        add(shopButton);
        setStars(0);
    }

    /**
     * Updates the star count here and in the shop.
     * TODO: call this from the board's state once BoardState has a stars field (Mavi, step 3).
     */
    public void setStars(int stars) {
        starsLabel.setText("★ " + stars);
        shop.setStars(stars);
    }
}
