package data_access;

import entity.Sticker;
import org.junit.jupiter.api.Test;
import use_case.search_sticker.SearchStickerDataAccessInterface;
import use_case.search_sticker.StickerSearchException;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombinedStickerDataAccessObjectTest {

    private static final SearchStickerDataAccessInterface FAILING = query -> {
        throw new StickerSearchException("down");
    };

    private static Sticker sticker(String id) {
        return new Sticker(id, id, "https://example.com/" + id + ".png");
    }

    private static SearchStickerDataAccessInterface returning(Sticker... stickers) {
        return query -> List.of(stickers);
    }

    private static List<String> ids(List<Sticker> stickers) {
        return stickers.stream().map(Sticker::getId).collect(Collectors.toList());
    }

    @Test
    void mergesResultsByTakingTurns() throws StickerSearchException {
        CombinedStickerDataAccessObject combined = new CombinedStickerDataAccessObject(List.of(
                returning(sticker("a1"), sticker("a2"), sticker("a3")),
                returning(sticker("b1"))));

        assertEquals(List.of("a1", "b1", "a2", "a3"), ids(combined.searchStickers("cat")));
    }

    @Test
    void oneSourceFailingStillReturnsTheOther() throws StickerSearchException {
        CombinedStickerDataAccessObject combined = new CombinedStickerDataAccessObject(List.of(
                FAILING, returning(sticker("b1"))));

        assertEquals(List.of("b1"), ids(combined.searchStickers("cat")));
    }

    @Test
    void allSourcesFailingThrows() {
        CombinedStickerDataAccessObject combined = new CombinedStickerDataAccessObject(List.of(
                FAILING, FAILING));

        assertThrows(StickerSearchException.class, () -> combined.searchStickers("cat"));
    }

    @Test
    void noResultsAnywhereReturnsEmptyList() throws StickerSearchException {
        CombinedStickerDataAccessObject combined = new CombinedStickerDataAccessObject(List.of(
                returning(), returning()));

        assertTrue(combined.searchStickers("zzqq").isEmpty());
    }
}