package task_tracker;

import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Task;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static Task fromString(String s) {
        List<String> parts = new ArrayList<>(List.of(s.split(",")));

        int id = Integer.parseInt(parts.get(0));
        String name = parts.get(2);
        Status status = Status.valueOf(parts.get(3));
        String description = parts.get(4);

        return new Task(name, description, status, id);
    }

    public static String historyToCsvString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        List<Task> history = historyManager.getHistory();
        for (Task task : history) {
            sb.append(task.getID()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static List<Integer> historyFromString(String s) {
        List<String> parts = new ArrayList<>(List.of(s.split(",")));
        List<Integer> result = new ArrayList<>();

        for (String part : parts) {
            result.add(Integer.parseInt(part));
        }
        return result;
    }
}
