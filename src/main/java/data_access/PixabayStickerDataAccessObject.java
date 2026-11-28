package data_access;

import entity.Sticker;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.search_sticker.SearchStickerDataAccessInterface;
import use_case.search_sticker.StickerSearchException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches still vector illustrations on Pixabay and converts them into Sticker entities.
 * Pixabay URLs may only be used to display search results, not stored permanently.
 */
public class PixabayStickerDataAccessObject implements SearchStickerDataAccessInterface {
    private static final String SEARCH_URL = "https://pixabay.com/api/";
    private static final int LIMIT = 12;

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public PixabayStickerDataAccessObject(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public List<Sticker> searchStickers(String query) throws StickerSearchException {
        if (apiKey == null || apiKey.isBlank()) {
            throw new StickerSearchException("PIXABAY_API_KEY is not set");
        }
        HttpUrl url = HttpUrl.parse(SEARCH_URL).newBuilder()
                .addQueryParameter("key", apiKey)
                .addQueryParameter("q", query)
                .addQueryParameter("image_type", "vector")
                .addQueryParameter("safesearch", "true")
                .addQueryParameter("per_page", String.valueOf(LIMIT))
                .build();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new StickerSearchException("Pixabay returned " + response.code());
            }
            JSONObject json = new JSONObject(response.body().string());
            return parseStickers(json.getJSONArray("hits"));
        } catch (IOException | JSONException ex) {
            throw new StickerSearchException("Could not reach Pixabay: " + ex.getMessage());
        }
    }

    /** Adapts Pixabay's JSON format into our Sticker entity. */
    private List<Sticker> parseStickers(JSONArray hits) {
        List<Sticker> stickers = new ArrayList<>();
        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            String id = "pixabay-" + hit.get("id");
            String title = hit.optString("tags", "").split(",")[0].trim();
            stickers.add(new Sticker(id, title, hit.getString("previewURL")));
        }
        return stickers;
    }
}