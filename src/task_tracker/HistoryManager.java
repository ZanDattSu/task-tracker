package task_tracker;

import task_tracker.tasks_type.Task;

import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

     void add(Task task);
}
