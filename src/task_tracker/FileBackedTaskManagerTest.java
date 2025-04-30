package task_tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_tracker.tasks_type.Epic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("test-status.csv");
        clearFile(file);
        taskManager = FileBackedTaskManager.loadFromFile(file);
        super.setup();
    }

    @Test
    public void shouldLoadEmptyTaskListFromFile() {
        clearFile(file);
        taskManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadEpicWithoutSubtasks() {
        Epic epic = taskManager.addEpic(new Epic("Epic"));
        taskManager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        Epic loadedEpic = loaded.getEpic(epic.getID());

        assertNotNull(loadedEpic);
        assertTrue(loadedEpic.getSubtasks().isEmpty());
    }

    @Test
    public void shouldLoadEmptyHistoryFromFile() {
        taskManager.addEpic(new Epic("Epic"));
        taskManager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getHistory().isEmpty());
    }

    private void clearFile(File file) {
        try (Writer writer = new FileWriter(file)) {
            writer.write("");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файлов!", e);
        }
    }
}