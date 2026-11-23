package data_access;

import entity.Sticker;
import use_case.search_sticker.SearchStickerDataAccessInterface;
import use_case.search_sticker.StickerSearchException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * Remembers recent search results so repeat searches are instant (Decorator pattern).
 * Pixabay requires results to be cached for 24 hours; this also saves GIPHY's rate limit.
 */
public class CachingStickerDataAccessObject implements SearchStickerDataAccessInterface {
    private static final long ONE_DAY_MILLIS = 24L * 60 * 60 * 1000;

    private final SearchStickerDataAccessInterface inner;
    private final long timeToLiveMillis;
    private final LongSupplier clock;
    private final Map<String, CachedResult> cache = new HashMap<>();

    public CachingStickerDataAccessObject(SearchStickerDataAccessInterface inner) {
        this(inner, ONE_DAY_MILLIS, System::currentTimeMillis);
    }

    /** Lets tests control the cache lifetime and the current time. */
    public CachingStickerDataAccessObject(SearchStickerDataAccessInterface inner,
                                          long timeToLiveMillis, LongSupplier clock) {
        this.inner = inner;
        this.timeToLiveMillis = timeToLiveMillis;
        this.clock = clock;
    }

    @Override
    public synchronized List<Sticker> searchStickers(String query) throws StickerSearchException {
        String key = query.trim().toLowerCase(Locale.ROOT);
        long now = clock.getAsLong();
        CachedResult cached = cache.get(key);
        if (cached != null && now - cached.savedAt < timeToLiveMillis) {
            return cached.stickers;
        }
        List<Sticker> stickers = List.copyOf(inner.searchStickers(query));
        cache.put(key, new CachedResult(stickers, now));
        return stickers;
    }

    /** A cached list of stickers and when it was saved. */
    private static final class CachedResult {
        private final List<Sticker> stickers;
        private final long savedAt;

        private CachedResult(List<Sticker> stickers, long savedAt) {
            this.stickers = stickers;
            this.savedAt = savedAt;
        }
    }
}