package task_tracker.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_tracker.HistoryManager;
import task_tracker.InMemoryHistoryManager;
import task_tracker.InMemoryTaskManager;
import task_tracker.TaskManager;
import task_tracker.tasks_type.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;


    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new InMemoryTaskManager();
        task1 = taskManager.addTask(new Task("Task 1", "Desc"));
        task2 = taskManager.addTask(new Task("Task 2", "Desc"));
        task3 = taskManager.addTask(new Task("Task 3", "Desc"));
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoTasksViewed() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldNotDuplicateTasksInHistory() {
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromBeginningOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getID());

        assertFalse(historyManager.getHistory().contains(task1));
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getID());

        assertFalse(historyManager.getHistory().contains(task2));
        assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getID());

        assertFalse(historyManager.getHistory().contains(task3));
        assertEquals(2, historyManager.getHistory().size());
    }
}