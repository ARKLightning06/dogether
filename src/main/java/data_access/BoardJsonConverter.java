package data_access;

import entity.Board;
import entity.Permission;
import entity.PlacedSticker;
import entity.Sticker;
import entity.Task;
import entity.TaskStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Converts a Board to the JSON stored in Firebase and back.
 * Kept separate from the HTTP code so it can be tested without the internet.
 */
public class BoardJsonConverter {

    /** Turns stored image data for a sticker into an address the app can display. */
    public interface ImageStore {
        /**
         * Keeps a sticker's image and returns an address for it.
         *
         * @param placementId the placed sticker's id
         * @param base64Data the image, base64-encoded
         * @return an address to display the image from
         */
        String store(String placementId, String base64Data);
    }

    /**
     * Converts a board to JSON.
     *
     * @param board the board
     * @param imageData base64 image data by placement id, for stickers whose image must be stored
     * @return the JSON to save
     */
    public JSONObject toJson(Board board, Map<String, String> imageData) {
        final JSONObject json = new JSONObject();
        json.put("title", board.getTitle());
        json.put("owner", board.getOwnerUsername());

        final JSONObject members = new JSONObject();
        for (Map.Entry<String, Permission> member : board.getMembers().entrySet()) {
            members.put(member.getKey(), member.getValue().name());
        }
        json.put("members", members);

        final JSONObject tasks = new JSONObject();
        for (Task task : board.getTasks()) {
            tasks.put(task.getId(), taskToJson(task));
        }
        json.put("tasks", tasks);

        final JSONObject stickers = new JSONObject();
        for (PlacedSticker sticker : board.getStickers()) {
            stickers.put(sticker.getPlacementId(), stickerToJson(sticker, imageData));
        }
        json.put("stickers", stickers);
        return json;
    }

    /**
     * Rebuilds a board from JSON.
     *
     * @param boardId the board's id
     * @param json the stored JSON
     * @param imageStore where stored sticker images are kept for display
     * @param imageDataOut filled with base64 image data by placement id, so it can be saved again
     * @return the board
     */
    public Board fromJson(String boardId, JSONObject json, ImageStore imageStore,
                          Map<String, String> imageDataOut) {
        final Board board = new Board(boardId, json.optString("title", ""), json.getString("owner"));

        final JSONObject members = json.optJSONObject("members");
        if (members != null) {
            for (String username : members.keySet()) {
                board.addMember(username, Permission.valueOf(members.getString(username)));
            }
        }

        final JSONObject tasks = json.optJSONObject("tasks");
        if (tasks != null) {
            for (String taskId : tasks.keySet()) {
                board.addTask(taskFromJson(taskId, tasks.getJSONObject(taskId)));
            }
        }

        final JSONObject stickers = json.optJSONObject("stickers");
        if (stickers != null) {
            for (String placementId : stickers.keySet()) {
                board.addSticker(stickerFromJson(placementId, stickers.getJSONObject(placementId),
                        imageStore, imageDataOut));
            }
        }
        return board;
    }

    private JSONObject taskToJson(Task task) {
        final JSONObject json = new JSONObject();
        json.put("name", task.getName());
        json.put("description", task.getDescription());
        if (task.getDeadline() != null) {
            json.put("deadline", task.getDeadline().toString());
        }
        json.put("colour", task.getColour());
        json.put("status", task.getStatus().name());
        json.put("assignees", new JSONArray(task.getAssignees()));
        json.put("completedBy", new JSONArray(task.getCompletedBy()));
        return json;
    }

    private Task taskFromJson(String taskId, JSONObject json) {
        final String name = json.getString("name");
        final String description = json.optString("description", "");
        LocalDate deadline = null;
        if (json.has("deadline")) {
            deadline = LocalDate.parse(json.getString("deadline"));
        }
        final String colour = json.getString("colour");
        final List<String> assignees = toStringList(json.optJSONArray("assignees"));

        final Task task = new Task(taskId, name, description, deadline, colour, assignees);
        for (String username : toStringList(json.optJSONArray("completedBy"))) {
            task.markCompleteBy(username);
        }
        // update() keeps the status consistent with who has completed the task
        task.update(name, description, deadline, colour, assignees,
                TaskStatus.valueOf(json.optString("status", TaskStatus.TO_DO.name())));
        return task;
    }

    private JSONObject stickerToJson(PlacedSticker placed, Map<String, String> imageData) {
        final JSONObject json = new JSONObject();
        json.put("stickerId", placed.getSticker().getId());
        json.put("title", placed.getSticker().getTitle());
        json.put("x", placed.getX());
        json.put("y", placed.getY());
        json.put("addedBy", placed.getAddedBy());
        final String data = imageData.get(placed.getPlacementId());
        if (data != null) {
            json.put("imageData", data);
        } else {
            json.put("imageUrl", placed.getSticker().getImageUrl());
        }
        return json;
    }

    private PlacedSticker stickerFromJson(String placementId, JSONObject json, ImageStore imageStore,
                                          Map<String, String> imageDataOut) {
        String imageUrl = json.optString("imageUrl", "");
        if (json.has("imageData")) {
            final String data = json.getString("imageData");
            imageDataOut.put(placementId, data);
            imageUrl = imageStore.store(placementId, data);
        }
        final Sticker sticker = new Sticker(json.getString("stickerId"), json.optString("title", ""), imageUrl);
        return new PlacedSticker(placementId, sticker, json.getInt("x"), json.getInt("y"),
                json.optString("addedBy", ""));
    }

    private static List<String> toStringList(JSONArray array) {
        final List<String> list = new ArrayList<>();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
        }
        return list;
    }
}
