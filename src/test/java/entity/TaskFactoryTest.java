package entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the creation of Task entities using TaskFactory.
 */
class TaskFactoryTest {
    private static final String NAME = "Study CSC207";
    private static final String DESCRIPTION = "Review Clean Architecture";
    private static final String COLOUR = "#FFFFFF";
    private static final String USERNAME = "Mostafa";

    private TaskFactory factory;

    /**
     * Initializes a new factory before each test.
     */
    @BeforeEach
    void setUp() {
        factory = new TaskFactory();
    }

    /**
     * Tests that the factory creates a task with the provided attributes.
     */
    @Test
    void testCreateValidTask() {
        LocalDate deadline = LocalDate.of(2026, 12, 1);

        Task task = factory.create(
                NAME, DESCRIPTION, deadline, COLOUR, List.of(USERNAME));

        assertNotNull(task);
        assertEquals(NAME, task.getName());
        assertEquals(DESCRIPTION, task.getDescription());
        assertEquals(deadline, task.getDeadline());
        assertEquals(COLOUR, task.getColour());
        assertEquals(List.of(USERNAME), task.getAssignees());
    }

    /**
     * Tests that newly created tasks start with TO_DO status.
     */
    @Test
    void testCreateInitialStatus() {
        Task task = factory.create(
                NAME, DESCRIPTION, null, COLOUR, List.of(USERNAME));

        assertEquals(TaskStatus.TO_DO, task.getStatus());
        assertTrue(task.getCompletedBy().isEmpty());
    }

    /**
     * Tests that separate factory calls generate distinct task IDs.
     */
    @Test
    void testCreateUniqueIds() {
        Task first = factory.create(
                "First task", "", null, COLOUR, List.of());

        Task second = factory.create(
                "Second task", "", null, COLOUR, List.of());

        assertNotEquals(first.getId(), second.getId());
    }

    /**
     * Tests that the generated task ID has a valid UUID format.
     */
    @Test
    void testCreateValidUuid() {
        Task task = factory.create(
                NAME, DESCRIPTION, null, COLOUR, List.of(USERNAME));

        UUID parsedId = UUID.fromString(task.getId());

        assertEquals(parsedId.toString(), task.getId());
    }

    /**
     * Tests that a task can be created without a deadline.
     */
    @Test
    void testCreateWithoutDeadline() {
        Task task = factory.create(
                NAME, DESCRIPTION, null, COLOUR, List.of(USERNAME));

        assertNull(task.getDeadline());
    }

    /**
     * Tests that the factory rejects an empty task name.
     */
    @Test
    void testCreateInvalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(
                        "", DESCRIPTION, null, COLOUR, List.of(USERNAME)));
    }

    /**
     * Tests that the factory rejects duplicate assignees.
     */
    @Test
    void testCreateDuplicateAssignees() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create(
                        NAME, DESCRIPTION, null, COLOUR,
                        List.of(USERNAME, USERNAME)));
    }
}
