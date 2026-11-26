package view;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;

/**
 * The sticker shop popup: the sticker search, plus how many stars the board has to spend.
 * Picking a sticker closes the shop and hands the sticker to the board.
 */
public class ShopDialog extends JDialog {
    private final JLabel starsLabel = new JLabel();

    /**
     * Creates the shop.
     *
     * @param owner the window it opens over
     * @param stickerPanel the sticker search panel
     * @param onPicked told which sticker was picked
     */
    public ShopDialog(Window owner, StickerPanel stickerPanel, StickerPickListener onPicked) {
        super(owner, "Sticker shop", ModalityType.MODELESS);

        final JLabel title = new JLabel("Sticker shop");
        title.setFont(StickerStyle.title(26f));
        title.setForeground(StickerStyle.TEXT);
        starsLabel.setFont(StickerStyle.body(Font.PLAIN, 13f));
        starsLabel.setForeground(StickerStyle.MUTED);

        final JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setBackground(StickerStyle.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(18, 20, 0, 20));
        header.add(title, BorderLayout.NORTH);
        header.add(starsLabel, BorderLayout.SOUTH);

        stickerPanel.setStickerPickListener((stickerId, imageUrl) -> {
            setVisible(false);
            onPicked.stickerPicked(stickerId, imageUrl);
        });

        final JPanel content = new JPanel(new BorderLayout());
        content.setBackground(StickerStyle.BACKGROUND);
        content.add(header, BorderLayout.NORTH);
        content.add(stickerPanel, BorderLayout.CENTER);
        setContentPane(content);
        setSize(580, 620);
        setLocationRelativeTo(owner);
        setStars(0);
    }

    /** Shows how many stars the board has. Each sticker costs one. */
    public void setStars(int stars) {
        String plural = "s";
        if (stars == 1) {
            plural = "";
        }
        starsLabel.setText("★ " + stars + " star" + plural + " to spend · each sticker costs 1 star");
    }
}
