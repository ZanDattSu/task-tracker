package task_tracker.managers;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;
import task_tracker.utils.TaskDataSerializer;

import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final File DEFAULT_STORAGE_FILE = new File("status.csv");
    private final File storageFile;

    public FileBackedTaskManager() {
        storageFile = DEFAULT_STORAGE_FILE;
    }

    public FileBackedTaskManager(File storageFile) {
        this.storageFile = storageFile;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Task removeTask(int id) {
        Task removedTask = super.removeTask(id);
        save();
        return removedTask;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask removedSubtask = super.removeSubtask(id);
        save();
        return removedSubtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Epic removeEpic(int id) {
        Epic removedEpic = super.removeEpic(id);
        save();
        return removedEpic;
    }

    protected void save() {
        TaskDataSerializer.saveToCsv(storageFile, getAllTasks(), getHistoryManager());
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            return manager;
        }
        try {
            TaskDataSerializer.loadIntoManager(file, manager);
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка при загрузке данных", e);
        }

        return manager;
    }
}