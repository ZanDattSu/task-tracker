package task_tracker;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> lastTenTask = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory() {
        return lastTenTask;
    }

    @Override
    public void add(Task task) {
        if (lastTenTask.size() == 10) {
            lastTenTask.removeFirst();
        }
        lastTenTask.add(task);
    }
}
