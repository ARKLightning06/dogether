package data_access;

import entity.Board;
import entity.PlacedSticker;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.DataAccessException;
import use_case.add_sticker.AddStickerDataAccessInterface;
import use_case.edit_task.EditTaskDataAccessInterface;
import use_case.filter_tasks_by_status.FilterTasksDataAccessInterface;
import use_case.view_completion_count.ViewCompletionCountDataAccessInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and saves boards in Firebase Realtime Database through its REST API.
 * Each board is stored at boards/{boardId}.
 *
 * <p>Pixabay doesn't allow its image links to be used permanently, so for Pixabay stickers
 * the image itself is saved in the database. GIPHY stickers keep their GIPHY link.
 */
public class FirebaseBoardDataAccessObject implements AddStickerDataAccessInterface,
        FilterTasksDataAccessInterface, ViewCompletionCountDataAccessInterface,
        EditTaskDataAccessInterface {
    private static final MediaType JSON = MediaType.get("application/json");
    private static final String STORED_IMAGE_PREFIX = "pixabay-";

    private final OkHttpClient client = new OkHttpClient();
    private final BoardJsonConverter converter = new BoardJsonConverter();
    private final String dbUrl;
    private final Map<String, String> imageDataByPlacement = new HashMap<>();
    private Path imageFolder;

    /**
     * Creates the data access object.
     *
     * @param dbUrl the Realtime Database URL, like https://dogether-xxxxx-default-rtdb.firebaseio.com
     */
    public FirebaseBoardDataAccessObject(String dbUrl) {
        if (dbUrl == null || dbUrl.isBlank()) {
            throw new IllegalArgumentException("FIREBASE_DB_URL is not set");
        }
        if (dbUrl.endsWith("/")) {
            this.dbUrl = dbUrl.substring(0, dbUrl.length() - 1);
        } else {
            this.dbUrl = dbUrl;
        }
    }

    @Override
    public synchronized Board getBoard(String boardId) {
        final Request request = new Request.Builder().url(boardUrl(boardId)).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new DataAccessException("Firebase returned " + response.code());
            }
            final String text = response.body().string().trim();
            if ("null".equals(text)) {
                return null;
            }
            return converter.fromJson(boardId, new JSONObject(text), this::storeImage, imageDataByPlacement);
        } catch (IOException | JSONException ex) {
            throw new DataAccessException("Couldn't load board " + boardId, ex);
        }
    }

    @Override
    public synchronized void saveBoard(Board board) {
        try {
            for (PlacedSticker sticker : board.getStickers()) {
                if (sticker.getSticker().getId().startsWith(STORED_IMAGE_PREFIX)
                        && !imageDataByPlacement.containsKey(sticker.getPlacementId())) {
                    imageDataByPlacement.put(sticker.getPlacementId(),
                            downloadBase64(sticker.getSticker().getImageUrl()));
                }
            }
            final String body = converter.toJson(board, imageDataByPlacement).toString();
            final Request request = new Request.Builder()
                    .url(boardUrl(board.getId()))
                    .put(RequestBody.create(body, JSON))
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new DataAccessException("Firebase returned " + response.code());
                }
            }
        } catch (IOException ex) {
            throw new DataAccessException("Couldn't save board " + board.getId(), ex);
        }
    }

    private String boardUrl(String boardId) {
        return dbUrl + "/boards/" + boardId + ".json";
    }

    private static String downloadBase64(String imageUrl) throws IOException {
        try (InputStream in = URI.create(imageUrl).toURL().openStream()) {
            return Base64.getEncoder().encodeToString(in.readAllBytes());
        }
    }

    /** Writes a stored image to a temporary file so the app can display it. */
    private String storeImage(String placementId, String base64Data) {
        try {
            if (imageFolder == null) {
                imageFolder = Files.createTempDirectory("dogether-stickers");
            }
            final Path file = imageFolder.resolve(placementId + ".img");
            Files.write(file, Base64.getDecoder().decode(base64Data));
            return file.toUri().toString();
        } catch (IOException | IllegalArgumentException ex) {
            return "";
        }
    }
}
