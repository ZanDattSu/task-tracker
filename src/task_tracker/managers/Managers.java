package task_tracker.managers;

import java.io.File;

public class Managers {
    public static HTTPTaskManager getDefault() {
        return new HTTPTaskManager();
    }

    public static InMemoryTaskManager getInMemory() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBacked(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
