package data_access;

import entity.Sticker;
import use_case.search_sticker.SearchStickerDataAccessInterface;
import use_case.search_sticker.StickerSearchException;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches several sticker sources and merges their results (Composite pattern).
 * If one source fails, results from the others are still returned.
 * It only fails if every source fails.
 */
public class CombinedStickerDataAccessObject implements SearchStickerDataAccessInterface {
    private final List<SearchStickerDataAccessInterface> sources;

    public CombinedStickerDataAccessObject(List<SearchStickerDataAccessInterface> sources) {
        this.sources = List.copyOf(sources);
    }

    @Override
    public List<Sticker> searchStickers(String query) throws StickerSearchException {
        List<List<Sticker>> resultsPerSource = new ArrayList<>();
        int failures = 0;
        for (SearchStickerDataAccessInterface source : sources) {
            try {
                resultsPerSource.add(source.searchStickers(query));
            } catch (StickerSearchException ex) {
                failures++;
            }
        }
        if (!sources.isEmpty() && failures == sources.size()) {
            throw new StickerSearchException("All sticker sources failed");
        }
        return interleave(resultsPerSource);
    }

    /** Takes one result from each source in turn, so the grid mixes both styles. */
    private List<Sticker> interleave(List<List<Sticker>> lists) {
        List<Sticker> merged = new ArrayList<>();
        int longest = 0;
        for (List<Sticker> list : lists) {
            longest = Math.max(longest, list.size());
        }
        for (int i = 0; i < longest; i++) {
            for (List<Sticker> list : lists) {
                if (i < list.size()) {
                    merged.add(list.get(i));
                }
            }
        }
        return merged;
    }
}