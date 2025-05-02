package task_tracker.tests;

import org.junit.jupiter.api.Test;
import task_tracker.managers.InMemoryTaskManager;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static task_tracker.utils.Parser.taskFromString;

class ParserTest {

    @Test
    void taskFromStringTest() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task t = new Task("Name", "Desc", Status.IN_PROGRESS,
                LocalDateTime.now().minusDays(1), Duration.ofHours(5));
        Epic e = new Epic("Name", "Desc");
        Task task = taskManager.addTask(t);
        Epic epic = taskManager.addEpic(e);

        Subtask st = new Subtask("Name", "Desc", Status.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofHours(5), epic.getID());
        Subtask subtask = taskManager.addSubtask(st);

        String[] stringTask = task.toCsvString().split(",");
        String[] stringSubtask = subtask.toCsvString().split(",");

        Task task1 = taskFromString(stringTask);
        Task subtask1 = taskFromString(stringSubtask);

        assertEquals(task, task1);
        assertEquals(subtask, subtask1);

    }
}