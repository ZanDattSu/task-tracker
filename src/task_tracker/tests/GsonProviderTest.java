package task_tracker.tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_tracker.managers.InMemoryTaskManager;
import task_tracker.managers.Managers;
import task_tracker.server.GsonProvider;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GsonProviderTest {
    private static final Gson gson = GsonProvider.getGson();
    private Task exampleTask;
    private Epic exampleEpic;
    private Subtask earlySubt;
    private Subtask laterSubt;

    @BeforeEach
    void setup() {
        InMemoryTaskManager taskManager = Managers.getDefault();
        LocalDateTime exampleTaskStart = LocalDateTime.of(2026, 1, 1, 12, 0);
        Duration exampleTaskDuration = Duration.ofMinutes(90);
        LocalDateTime earlySubtStart = LocalDateTime.of(2025, 3, 3, 9, 0);
        Duration earlySubtDuration = Duration.ofMinutes(30);
        LocalDateTime laterSubtStart = LocalDateTime.of(2025, 3, 3, 10, 0);
        Duration laterSubtDuration = Duration.ofMinutes(60);

        exampleTask = taskManager.addTask(new Task("Task 1", "Task Desc 1",
                Status.NEW, exampleTaskStart, exampleTaskDuration));
        exampleEpic = taskManager.addEpic(new Epic("Epic 1", "Epic Desc 1"));
        earlySubt = taskManager.addSubtask((new Subtask("Sub 1", "Sub Desc 1",
                Status.NEW, earlySubtStart, earlySubtDuration, exampleEpic.getID())), exampleEpic);
        laterSubt = taskManager.addSubtask(
                new Subtask("Sub 2", "Sub Desc 2",
                        Status.NEW, laterSubtStart, laterSubtDuration, exampleEpic.getID()));
    }

    @Test
    void test() {
        String gsonTask = gson.toJson(exampleTask);
        System.out.println(gsonTask);

        Task task = gson.fromJson(gsonTask, Task.class);
        assertEquals(task, exampleTask);


        String gsonSubTask = gson.toJson(earlySubt);
        System.out.println(gsonSubTask);

        Subtask subtask = gson.fromJson(gsonSubTask, Subtask.class);
        assertEquals(subtask, earlySubt);

        String gsonSubTask1 = gson.toJson(laterSubt);
        System.out.println(gsonSubTask1);

        Subtask subtask1 = gson.fromJson(gsonSubTask1, Subtask.class);
        assertEquals(subtask1, laterSubt);


        String gsonEpic = gson.toJson(exampleEpic);
        System.out.println(gsonEpic);

        Epic epic = gson.fromJson(gsonEpic, Epic.class);
        assertEquals(epic, exampleEpic);
    }
}