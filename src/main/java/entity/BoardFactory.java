package entity;

import java.util.UUID;

public class BoardFactory {
    public Board create(String title, String ownerUsername) {
        return new Board(UUID.randomUUID().toString(), title, ownerUsername);
    }
}