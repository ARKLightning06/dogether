package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

/**
 * Colours and fonts for the sticker screens, from the team's UI doc.
 * TODO: switch to Mavi's view.Theme once it's merged, then delete this class.
 */
public final class StickerStyle {
    public static final Color BACKGROUND = new Color(0xF7F3EC);
    public static final Color WHITEBOARD = Color.WHITE;
    public static final Color DOTS = new Color(0xECE6DB);
    public static final Color FRAME = new Color(0xD9D6D0);
    public static final Color TEXT = new Color(0x2E2A26);
    public static final Color MUTED = new Color(0x6E675F);
    public static final Color PINK = new Color(0xF9D5E2);
    public static final Color MINT = new Color(0xC9EEDD);
    public static final Color BUTTER = new Color(0xFBE3B2);
    public static final Color LAVENDER = new Color(0xDEDAFB);
    public static final Color SKY = new Color(0xD3E6F9);
    public static final Color ERROR = new Color(0xA32D2D);
    public static final Color STAR = new Color(0xE0A526);

    private static final String TITLE_FONT = "Cormorant Garamond";
    private static final String BODY_FONT = "DM Sans";

    private StickerStyle() {
    }

    /** Title font, or a serif fallback if Cormorant Garamond isn't installed. */
    public static Font title(float size) {
        return font(TITLE_FONT, Font.SERIF, Font.BOLD, size);
    }

    /** Body font, or a sans-serif fallback if DM Sans isn't installed. */
    public static Font body(int style, float size) {
        return font(BODY_FONT, Font.SANS_SERIF, style, size);
    }

    private static Font font(String wanted, String fallback, int style, float size) {
        final String[] installed = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        final String family = Arrays.asList(installed).contains(wanted) ? wanted : fallback;
        return new Font(family, style, Math.round(size)).deriveFont(size);
    }
}
