package task_tracker.managers;

import task_tracker.tasks_type.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();

    void clearHistory();
}
