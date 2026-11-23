package data_access;

import entity.Board;
import entity.Permission;
import entity.PlacedSticker;
import entity.Sticker;
import entity.Task;
import entity.TaskStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardJsonConverterTest {
    private final BoardJsonConverter converter = new BoardJsonConverter();
    private final BoardJsonConverter.ImageStore fakeStore = (placementId, data) -> "stored://" + placementId;
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board("b1", "Team board", "marina");
        board.addMember("andrew", Permission.VIEW);
    }

    /** Saves to JSON text and reads it back, like a round trip through Firebase. */
    private Board roundTrip(Map<String, String> imageData, Map<String, String> imageDataOut) {
        final String text = converter.toJson(board, imageData).toString();
        return converter.fromJson("b1", new JSONObject(text), fakeStore, imageDataOut);
    }

    @Test
    void keepsTitleOwnerAndMembers() {
        final Board loaded = roundTrip(Map.of(), new HashMap<>());

        assertEquals("Team board", loaded.getTitle());
        assertEquals("marina", loaded.getOwnerUsername());
        assertTrue(loaded.canEdit("marina"));
        assertTrue(loaded.hasMember("andrew"));
        assertFalse(loaded.canEdit("andrew"));
    }

    @Test
    void keepsTasksAndWhoCompletedThem() {
        final Task done = new Task("t1", "Done task", "desc", LocalDate.of(2026, 12, 1), "#FFFFFF",
                List.of("marina"));
        done.markCompleteBy("marina");
        final Task half = new Task("t2", "Shared task", "", null, "#000000", List.of("marina", "andrew"));
        half.markCompleteBy("andrew");
        board.addTask(done);
        board.addTask(half);

        final Board loaded = roundTrip(Map.of(), new HashMap<>());

        final Task loadedDone = loaded.getTask("t1");
        assertEquals(TaskStatus.DONE, loadedDone.getStatus());
        assertEquals(LocalDate.of(2026, 12, 1), loadedDone.getDeadline());
        final Task loadedHalf = loaded.getTask("t2");
        assertEquals(Set.of("andrew"), loadedHalf.getCompletedBy());
        assertEquals(List.of("marina", "andrew"), loadedHalf.getAssignees());
        assertFalse(loadedHalf.isFullyComplete());
        assertNull(loadedHalf.getDeadline());
    }

    @Test
    void keepsGiphyStickerLink() {
        board.addSticker(new PlacedSticker("p1", new Sticker("giphy1", "cat", "https://media.giphy.com/c.gif"),
                40, 60, "marina"));

        final Board loaded = roundTrip(Map.of(), new HashMap<>());

        final PlacedSticker sticker = loaded.getStickers().get(0);
        assertEquals("p1", sticker.getPlacementId());
        assertEquals("https://media.giphy.com/c.gif", sticker.getSticker().getImageUrl());
        assertEquals(40, sticker.getX());
        assertEquals(60, sticker.getY());
        assertEquals("marina", sticker.getAddedBy());
    }

    @Test
    void storesImageDataInsteadOfLinkWhenGiven() {
        board.addSticker(new PlacedSticker("p2", new Sticker("pixabay-9", "star", "https://cdn.pixabay.com/s.png"),
                0, 0, "marina"));
        final Map<String, String> imageDataOut = new HashMap<>();

        final JSONObject json = converter.toJson(board, Map.of("p2", "QUJD"));
        final Board loaded = converter.fromJson("b1", new JSONObject(json.toString()), fakeStore, imageDataOut);

        final JSONObject stored = json.getJSONObject("stickers").getJSONObject("p2");
        assertFalse(stored.has("imageUrl"));
        assertEquals("stored://p2", loaded.getStickers().get(0).getSticker().getImageUrl());
        assertEquals("QUJD", imageDataOut.get("p2"));
    }

    @Test
    void emptyBoardLoadsWithNoTasksOrStickers() {
        final Board loaded = converter.fromJson("b1", new JSONObject("{\"title\":\"x\",\"owner\":\"marina\"}"),
                fakeStore, new HashMap<>());

        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getStickers().isEmpty());
    }
}
