package data_access;

import entity.Sticker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.search_sticker.SearchStickerDataAccessInterface;
import use_case.search_sticker.StickerSearchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CachingStickerDataAccessObjectTest {

    /** Counts how many times the real search was called. */
    private static class CountingSource implements SearchStickerDataAccessInterface {
        private int calls;
        private boolean fail;

        @Override
        public List<Sticker> searchStickers(String query) throws StickerSearchException {
            calls++;
            if (fail) {
                throw new StickerSearchException("down");
            }
            return List.of(new Sticker(query + calls, query, "https://example.com/x.png"));
        }
    }

    private CountingSource source;
    private long now;
    private CachingStickerDataAccessObject cache;

    @BeforeEach
    void setUp() {
        source = new CountingSource();
        now = 0;
        cache = new CachingStickerDataAccessObject(source, 1000, () -> now);
    }

    @Test
    void repeatSearchUsesCache() throws StickerSearchException {
        cache.searchStickers("cat");
        cache.searchStickers("cat");

        assertEquals(1, source.calls);
    }

    @Test
    void caseAndSpacesDoNotMatter() throws StickerSearchException {
        cache.searchStickers("Cat");
        cache.searchStickers("  cat ");

        assertEquals(1, source.calls);
    }

    @Test
    void expiredEntryIsFetchedAgain() throws StickerSearchException {
        cache.searchStickers("cat");
        now = 1000;
        cache.searchStickers("cat");

        assertEquals(2, source.calls);
    }

    @Test
    void failuresAreNotCached() {
        source.fail = true;
        assertThrows(StickerSearchException.class, () -> cache.searchStickers("cat"));
        assertThrows(StickerSearchException.class, () -> cache.searchStickers("cat"));

        assertEquals(2, source.calls);
    }
}
