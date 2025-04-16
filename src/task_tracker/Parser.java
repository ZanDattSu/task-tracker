package task_tracker;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static Task taskFromString(String[] s) {
        Integer id = Integer.parseInt(s[0]);
        String name = s[2];
        Status status = Status.valueOf(s[3]);
        String description = s[4];
        if (s[1].equals("TASK")) {
            return new Task(name, description, status, id);
        } else if (s[1].equals("EPIC")) {
            return new Epic(name, description, id);
        } else {
            Integer epicID = Integer.parseInt(s[5]);
            return new Subtask(name, description, status, id, epicID);
        }
    }

    public static String historyToCsvString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        List<Task> history = historyManager.getHistory();
        for (Task task : history) {
            sb.append(task.getID()).append(",");
        }
        if (!sb.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static List<Integer> historyFromString(String[] s) {
        List<Integer> result = new ArrayList<>();

        for (String part : s) {
            result.add(Integer.parseInt(part));
        }
        return result;
    }
}
