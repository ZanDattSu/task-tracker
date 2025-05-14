package task_tracker.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_tracker.managers.Managers;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;
import task_tracker.utils.CsvParser;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvParserTest {
    private Task task;
    private Subtask subtask;

    @BeforeEach
    void setup() {
        TaskManager taskManager = Managers.getInMemory();
        Task t = new Task("Name", "Desc", Status.IN_PROGRESS,
                LocalDateTime.now().minusDays(1), Duration.ofHours(5));
        Epic e = new Epic("Name", "Desc");
        task = taskManager.addTask(t);
        Epic epic = taskManager.addEpic(e);

        Subtask st = new Subtask("Name", "Desc", Status.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofHours(5), epic.getID());
        subtask = taskManager.addSubtask(st);
    }

    @Test
    void taskFromStringTest() {
        String stringTask = CsvParser.taskToString(task);
        String stringSubtask = CsvParser.taskToString(subtask);

        Task task1 = CsvParser.taskFromString(stringTask);
        Task subtask1 = CsvParser.taskFromString(stringSubtask);

        assertEquals(task, task1);
        assertEquals(subtask, subtask1);
    }
}