package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.awt.Font;
import org.junit.jupiter.api.Test;

/**
 * Tests for the shared Theme.
 */
class ThemeTest {

    @Test
    void pastelsCycleThroughFiveColours() {
        assertEquals(Theme.PINK, Theme.pastel(0));
        assertEquals(Theme.SKY, Theme.pastel(4));
        assertEquals(Theme.PINK, Theme.pastel(5));
    }

    @Test
    void neighbouringListsGetDifferentPastels() {
        assertNotEquals(Theme.pastel(0), Theme.pastel(1));
    }

    @Test
    void negativeIndexStillGivesAPastel() {
        assertEquals(Theme.SKY, Theme.pastel(-1));
    }

    @Test
    void fontsUseTheRequestedSizeAndStyle() {
        final Font body = Theme.body(Font.PLAIN, 13f);
        assertEquals(13f, body.getSize2D());
        assertEquals(Font.PLAIN, body.getStyle());
        assertEquals(Font.BOLD, Theme.title(20f).getStyle());
    }
}
