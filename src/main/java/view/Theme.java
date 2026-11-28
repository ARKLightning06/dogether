package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.List;

/**
 * Shared colours and fonts for every DoGether screen, so they all match the
 * mockup. Use these instead of picking colours in each view.
 * The names match StickerStyle, so switching over is a find-and-replace.
 */
public final class Theme {
    /** Page background behind the whiteboard. */
    public static final Color BACKGROUND = new Color(0xF7F3EC);
    /** The whiteboard surface. */
    public static final Color WHITEBOARD = Color.WHITE;
    /** The dots on the whiteboard, drawn every DOT_SPACING pixels. */
    public static final Color DOTS = new Color(0xECE6DB);
    /** The whiteboard's rounded frame and thin borders. */
    public static final Color FRAME = new Color(0xD9D6D0);
    /** Thin line under each task. */
    public static final Color LINE = new Color(0xEFEAE2);
    /** Main text. */
    public static final Color TEXT = new Color(0x2E2A26);
    /** Small and secondary text. */
    public static final Color MUTED = new Color(0x6E675F);
    /** Error messages. */
    public static final Color ERROR = new Color(0xA32D2D);
    /** The star icon and star count. */
    public static final Color STAR = new Color(0xE0A526);

    /** Pastel pink, for highlights and board cards. */
    public static final Color PINK = new Color(0xF9D5E2);
    /** Pastel mint, for highlights and board cards. */
    public static final Color MINT = new Color(0xC9EEDD);
    /** Pastel butter yellow, for highlights and board cards. */
    public static final Color BUTTER = new Color(0xFBE3B2);
    /** Pastel lavender, for highlights and board cards. */
    public static final Color LAVENDER = new Color(0xDEDAFB);
    /** Pastel sky blue, for highlights and board cards. */
    public static final Color SKY = new Color(0xD3E6F9);

    /** Empty part of a progress bar. */
    public static final Color PROGRESS_TRACK = new Color(0xEFEAE2);
    /** Filled part of a progress bar. */
    public static final Color PROGRESS_FILL = new Color(0x8FD3B8);

    /** Space between the whiteboard dots, in pixels. */
    public static final int DOT_SPACING = 20;

    private static final String TITLE_FONT = "Cormorant Garamond";
    private static final String BODY_FONT = "DM Sans";
    private static final List<Color> PASTELS = List.of(PINK, MINT, BUTTER, LAVENDER, SKY);

    private Theme() {
    }

    /**
     * Returns the title font, or a serif font if Cormorant Garamond isn't installed.
     *
     * @param size the font size
     * @return the title font
     */
    public static Font title(float size) {
        return font(TITLE_FONT, Font.SERIF, Font.BOLD, size);
    }

    /**
     * Returns the body font, or a sans-serif font if DM Sans isn't installed.
     *
     * @param style Font.PLAIN, Font.BOLD or Font.ITALIC
     * @param size the font size
     * @return the body font
     */
    public static Font body(int style, float size) {
        return font(BODY_FONT, Font.SANS_SERIF, style, size);
    }

    /**
     * Picks a pastel for the n-th list or board, cycling through the five pastels,
     * so neighbouring lists and board cards get different colours.
     *
     * @param index the position of the list or board, starting at 0
     * @return one of the five pastel colours
     */
    public static Color pastel(int index) {
        return PASTELS.get(Math.floorMod(index, PASTELS.size()));
    }

    private static Font font(String wanted, String fallback, int style, float size) {
        final String[] installed = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        final String family;
        if (Arrays.asList(installed).contains(wanted)) {
            family = wanted;
        } else {
            family = fallback;
        }
        return new Font(family, style, Math.round(size)).deriveFont(size);
    }
}
