package entity;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final int MAX_STICKERS = 20;

    private final String id;
    private String title;
    private final String ownerId;
    private final List<Task> tasks;
    private final List<String> memberUsernames;

    public Board(String id, String title, String ownerId) {
        this.id = id;
        this.title = title;
        this.ownerId = ownerId;
        this.tasks = new ArrayList<>();
        this.memberUsernames = new ArrayList<>();
        this.memberUsernames.add(ownerId);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOwnerId() { return ownerId; }
    public List<Task> getTasks() { return tasks; }
    public List<String> getMemberUsernames() { return memberUsernames; }

    public void addMember(String username) {
        if (!memberUsernames.contains(username)) memberUsernames.add(username);
    }

    public boolean hasMember(String username) {
        return memberUsernames.contains(username);
    }

    public void addTask(Task task) { tasks.add(task); }

    public boolean removeTask(String taskId) {
        return tasks.removeIf(t -> t.getId().equals(taskId));
    }

    public Task getTask(String taskId) {
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) return task;
        }
        return null;
    }
}