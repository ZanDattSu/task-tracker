package task_tracker;

import java.util.ArrayList;

public interface HistoryManager {

    ArrayList<Task> getHistory();

     void add(Task task);
}
