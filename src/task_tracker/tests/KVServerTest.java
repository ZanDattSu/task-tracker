package task_tracker.tests;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_tracker.managers.Managers;
import task_tracker.managers.TaskManager;
import task_tracker.server.GsonProvider;
import task_tracker.server.KVServer;
import task_tracker.server.KVTaskClient;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KVServerTest {
    private Gson gson;
    private Task task;
    private Epic epic;

    @BeforeEach
    void setup() throws IOException {
        new KVServer().start();
        gson = GsonProvider.getGson();
        TaskManager taskManager = Managers.getDefault();

        LocalDateTime taskStart = LocalDateTime.of(2026, 1, 1, 12, 0);
        Duration taskDuration = Duration.ofMinutes(90);
        task = taskManager.addTask(new Task("Task 1", "Task Desc 1",
                Status.NEW, taskStart, taskDuration));
        epic = taskManager.addEpic(new Epic("Epic 1", "Epic Desc 1"));
    }

    @Test
    void test() {
        KVTaskClient client = new KVTaskClient(KVServer.getUrl());
        String jsonTask = gson.toJson(task);
        String jsonEpic = gson.toJson(epic);

        String jsonTask1 = client.load(String.valueOf(task.getID()));
        assertEquals(jsonTask, jsonTask1);

        String jsonEpic1 = client.load(String.valueOf(epic.getID()));
        assertEquals(jsonEpic, jsonEpic1);
    }
}