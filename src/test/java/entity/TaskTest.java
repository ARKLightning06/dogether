package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the behaviour of the Task entity.
 */
class TaskTest {
    private static final String ID = "task-1";
    private static final String NAME = "Complete CSC207 project";
    private static final String DESCRIPTION = "Implement task functionality";
    private static final String COLOUR = "#FFFFFF";
    private static final String USER_ONE = "Mostafa";
    private static final String USER_TWO = "Andrew";
    private static final String USER_THREE = "Maria";

    private Task task;

    /**
     * Creates a new task before each test.
     */
    @BeforeEach
    void setUp() {
        task = new Task(
                ID,
                NAME,
                DESCRIPTION,
                LocalDate.of(2026, 12, 1),
                COLOUR,
                List.of(USER_ONE, USER_TWO));
    }

    /**
     * Tests that the constructor initializes all fields correctly.
     */
    @Test
    void testConstructorValidInput() {
        assertEquals(ID, task.getId());
        assertEquals(NAME, task.getName());
        assertEquals(DESCRIPTION, task.getDescription());
        assertEquals(LocalDate.of(2026, 12, 1), task.getDeadline());
        assertEquals(COLOUR, task.getColour());
        assertEquals(TaskStatus.TO_DO, task.getStatus());
        assertEquals(List.of(USER_ONE, USER_TWO), task.getAssignees());
        assertTrue(task.getCompletedBy().isEmpty());
    }

    /**
     * Tests that the constructor rejects an empty task name.
     */
    @Test
    void testConstructorEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task(
                        ID, "", DESCRIPTION, null, COLOUR, List.of(USER_ONE)));
    }

    /**
     * Tests that the constructor rejects duplicate assignees.
     */
    @Test
    void testConstructorDuplicateAssignees() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task(
                        ID, NAME, DESCRIPTION, null, COLOUR,
                        List.of(USER_ONE, USER_ONE)));
    }

    /**
     * Tests that assigned users are identified correctly.
     */
    @Test
    void testIsAssignedToAssignedUser() {
        assertTrue(task.isAssignedTo(USER_ONE));
        assertTrue(task.isAssignedTo(USER_TWO));
    }

    /**
     * Tests that unassigned users are not identified as assignees.
     */
    @Test
    void testIsAssignedToUnassignedUser() {
        assertFalse(task.isAssignedTo(USER_THREE));
    }

    /**
     * Tests that a newly created task has no completion records.
     */
    @Test
    void testIsCompletedByInitiallyFalse() {
        assertFalse(task.isCompletedBy(USER_ONE));
        assertFalse(task.isCompletedBy(USER_TWO));
    }

    /**
     * Tests that marking a task complete records the correct user.
     */
    @Test
    void testMarkCompleteByAssignedUser() {
        task.markCompleteBy(USER_ONE);

        assertTrue(task.isCompletedBy(USER_ONE));
        assertFalse(task.isCompletedBy(USER_TWO));
        assertEquals(1, task.getCompletedBy().size());
    }

    /**
     * Tests that an unassigned user cannot complete a task.
     */
    @Test
    void testMarkCompleteByUnassignedUser() {
        assertThrows(IllegalArgumentException.class,
                () -> task.markCompleteBy(USER_THREE));

        assertFalse(task.isCompletedBy(USER_THREE));
    }

    /**
     * Tests that a user cannot complete the same task twice.
     */
    @Test
    void testMarkCompleteByAlreadyCompleted() {
        task.markCompleteBy(USER_ONE);

        assertThrows(IllegalStateException.class,
                () -> task.markCompleteBy(USER_ONE));

        assertEquals(1, task.getCompletedBy().size());
    }

    /**
     * Tests that a task is incomplete when only one assignee finishes.
     */
    @Test
    void testIsFullyCompletePartialCompletion() {
        task.markCompleteBy(USER_ONE);

        assertFalse(task.isFullyComplete());
    }

    /**
     * Tests that a task becomes fully complete when all assignees finish.
     */
    @Test
    void testIsFullyCompleteAllCompleted() {
        task.markCompleteBy(USER_ONE);
        task.markCompleteBy(USER_TWO);

        assertTrue(task.isFullyComplete());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    /**
     * Tests that a task with no assignees is not fully complete.
     */
    @Test
    void testIsFullyCompleteNoAssignees() {
        Task emptyTask = new Task(
                "task-2", NAME, DESCRIPTION, null, COLOUR, List.of());

        assertFalse(emptyTask.isFullyComplete());
    }

    /**
     * Tests that updating a task changes its editable attributes.
     */
    @Test
    void testUpdateValidInput() {
        LocalDate deadline = LocalDate.of(2026, 12, 10);

        task.update(
                "Updated task",
                "Updated description",
                deadline,
                "#000000",
                List.of(USER_ONE),
                TaskStatus.IN_PROGRESS);

        assertEquals(ID, task.getId());
        assertEquals("Updated task", task.getName());
        assertEquals("Updated description", task.getDescription());
        assertEquals(deadline, task.getDeadline());
        assertEquals("#000000", task.getColour());
        assertEquals(List.of(USER_ONE), task.getAssignees());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    /**
     * Tests that updating a task preserves valid completion records.
     */
    @Test
    void testUpdatePreservesExistingCompletion() {
        task.markCompleteBy(USER_ONE);

        task.update(
                NAME, DESCRIPTION, null, COLOUR,
                List.of(USER_ONE, USER_THREE),
                TaskStatus.IN_PROGRESS);

        assertTrue(task.isCompletedBy(USER_ONE));
        assertFalse(task.isCompletedBy(USER_THREE));
    }

    /**
     * Tests that removing an assignee also removes their completion record.
     */
    @Test
    void testUpdateRemovesOldAssigneeCompletion() {
        task.markCompleteBy(USER_ONE);

        task.update(
                NAME, DESCRIPTION, null, COLOUR,
                List.of(USER_TWO),
                TaskStatus.TO_DO);

        assertFalse(task.isCompletedBy(USER_ONE));
        assertFalse(task.isFullyComplete());
    }

    /**
     * Tests that invalid updates do not modify the task.
     */
    @Test
    void testUpdateInvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> task.update(
                        "", DESCRIPTION, null, COLOUR,
                        List.of(USER_ONE), TaskStatus.IN_PROGRESS));

        assertEquals(NAME, task.getName());
        assertEquals(TaskStatus.TO_DO, task.getStatus());
        assertEquals(List.of(USER_ONE, USER_TWO), task.getAssignees());
    }

    /**
     * Tests that an invalid null status is rejected during an update.
     */
    @Test
    void testUpdateNullStatus() {
        assertThrows(NullPointerException.class,
                () -> task.update(
                        NAME, DESCRIPTION, null, COLOUR,
                        List.of(USER_ONE), null));

        assertEquals(TaskStatus.TO_DO, task.getStatus());
    }

    /**
     * Tests that the assignee list cannot be modified through its getter.
     */
    @Test
    void testGetAssigneesUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> task.getAssignees().add(USER_THREE));
    }

    /**
     * Tests that completion records cannot be modified through their getter.
     */
    @Test
    void testGetCompletedByUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> task.getCompletedBy().add(USER_THREE));
    }

    /**
     * Tests that changing the original input list does not modify the task.
     */
    @Test
    void testConstructorCopiesAssignees() {
        List<String> assignees = new ArrayList<>();
        assignees.add(USER_ONE);

        Task newTask = new Task(
                "task-2", NAME, DESCRIPTION, null, COLOUR, assignees);

        assignees.add(USER_TWO);

        assertEquals(List.of(USER_ONE), newTask.getAssignees());
    }
}
