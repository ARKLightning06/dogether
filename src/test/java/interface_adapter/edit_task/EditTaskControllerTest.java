package interface_adapter.edit_task;

import use_case.edit_task.EditTaskInputData;
import entity.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the Edit Task controller.
 */
class EditTaskControllerTest {
    private EditTaskInputData received;
    private EditTaskController controller;

    @BeforeEach
    void setUp() {
        controller = new EditTaskController(input -> received = input);
    }

    @Test
    void forwardsAllInputFields() {
        LocalDate deadline = LocalDate.of(2026, 12, 5);
        controller.execute("alice", "board1", "task1", "Updated", "Description",
                deadline, "#D4537E", List.of("bob"), TaskStatus.IN_PROGRESS);
        assertEquals("alice", received.getUsername());
        assertEquals("board1", received.getBoardId());
        assertEquals("task1", received.getTaskId());
        assertEquals("Updated", received.getName());
        assertEquals("Description", received.getDescription());
        assertEquals(deadline, received.getDeadline());
        assertEquals("#D4537E", received.getColour());
        assertEquals(List.of("bob"), received.getAssignees());
        assertEquals(TaskStatus.IN_PROGRESS, received.getStatus());
    }
}
