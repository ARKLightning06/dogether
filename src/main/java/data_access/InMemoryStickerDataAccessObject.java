package data_access;

import entity.PlacedSticker;
import use_case.add_sticker.AddStickerDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps stickers in memory. Used for tests and for running without Firebase.
 */
public class InMemoryStickerDataAccessObject implements AddStickerDataAccessInterface {
    private final Map<String, List<PlacedSticker>> stickersByBoard = new HashMap<>();
    private final Map<String, Integer> completedTasksByBoard = new HashMap<>();

    /** Stand-in until the real Board and Task storage exists. */
    public void setCompletedTasks(String boardId, int count) {
        completedTasksByBoard.put(boardId, count);
    }

    public List<PlacedSticker> getStickers(String boardId) {
        return stickersByBoard.getOrDefault(boardId, new ArrayList<>());
    }

    @Override
    public int countCompletedTasks(String boardId) {
        return completedTasksByBoard.getOrDefault(boardId, 0);
    }

    @Override
    public int countStickers(String boardId) {
        return getStickers(boardId).size();
    }

    @Override
    public void saveSticker(String boardId, PlacedSticker sticker) {
        stickersByBoard.computeIfAbsent(boardId, key -> new ArrayList<>()).add(sticker);
    }
}