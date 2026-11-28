package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    public static final int MAX_STICKERS = 20;

    private final String id;
    private String title;
    private final String ownerUsername;
    private final Map<String, Permission> members;
    private final List<Task> tasks;
    private final List<PlacedSticker> stickers;

    public Board(String id, String title, String ownerUsername) {
        this.id = id;
        this.title = title;
        this.ownerUsername = ownerUsername;
        this.members = new HashMap<>();
        this.members.put(ownerUsername, Permission.EDIT);
        this.tasks = new ArrayList<>();
        this.stickers = new ArrayList<>();
    }

    /**
     * Creates an independent board snapshot for editing before saving.
     *
     * @param source the board to copy
     */
    public Board(Board source) {
        this.id = source.id;
        this.title = source.title;
        this.ownerUsername = source.ownerUsername;
        this.members = new HashMap<>(source.members);
        this.tasks = new ArrayList<>();

        for (Task task : source.tasks) {
            this.tasks.add(new Task(task));
        }

        // PlacedSticker and Sticker are immutable.
        this.stickers = new ArrayList<>(source.stickers);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOwnerUsername() { return ownerUsername; }
    public Map<String, Permission> getMembers() { return members; }

    public boolean hasMember(String username) {
        return members.containsKey(username);
    }

    public boolean canEdit(String username) {
        return Permission.EDIT.equals(members.get(username));
    }

    public void addMember(String username, Permission permission) {
        members.put(username, permission);
    }

    public List<Task> getTasks() { return tasks; }

    public void addTask(Task task) { tasks.add(task); }

    public Task getTask(String taskId) {
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) return task;
        }
        return null;
    }

    public boolean isFull() { return stickers.size() >= MAX_STICKERS; }

    public List<PlacedSticker> getStickers() { return stickers; }

    public void addSticker(PlacedSticker sticker) { stickers.add(sticker); }

    public boolean removeSticker(String placementId) {
        return stickers.removeIf(s -> s.getPlacementId().equals(placementId));
    }
}