package interface_adapter.edit_task;

import entity.TaskStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Edit Task state.
 */
class EditTaskStateTest {
    private EditTaskState state;

    @BeforeEach
    void setUp() {
        state = new EditTaskState();
    }

    @Test
    void defaultStateIsReadyForAddTask() {
        assertEquals("", state.getTaskId());
        assertEquals("", state.getName());
        assertEquals("", state.getDescription());
        assertEquals("#FFFFFF", state.getColour());
        assertNull(state.getDeadline());
        assertEquals(TaskStatus.TO_DO, state.getStatus());
        assertTrue(state.getAssignees().isEmpty());
        assertEquals("", state.getError());
    }

    @Test
    void storesEditedFieldsAndClearsDeadline() {
        LocalDate deadline = LocalDate.of(2026, 12, 5);
        state.setTaskId("t1");
        state.setName("Updated");
        state.setDescription("Description");
        state.setDeadline(deadline);
        state.setColour("#D4537E");
        state.setStatus(TaskStatus.IN_PROGRESS);
        state.setError("Error");
        assertEquals("t1", state.getTaskId());
        assertEquals("Updated", state.getName());
        assertEquals("Description", state.getDescription());
        assertEquals(deadline, state.getDeadline());
        assertEquals("#D4537E", state.getColour());
        assertEquals(TaskStatus.IN_PROGRESS, state.getStatus());
        assertEquals("Error", state.getError());
        state.setDeadline(null);
        assertNull(state.getDeadline());
    }

    @Test
    void assigneesAreDefensivelyCopied() {
        List<String> input = new ArrayList<>(List.of("alice"));
        state.setAssignees(input);
        input.add("bob");
        state.getAssignees().clear();
        assertEquals(List.of("alice"), state.getAssignees());
    }
}
