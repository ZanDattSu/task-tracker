package task_tracker.utils;

import task_tracker.managers.HistoryManager;
import task_tracker.managers.ManagerSaveException;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    public static String taskToString(Task task) {
        String csvTask;
        if (task instanceof Epic) {
            csvTask = String.format("%d,EPIC,%s,%s,%s,%s,%s",
                    task.getID(), task.getName(), task.getStatus(), task.getDescription(),
                    task.getStartTime(), task.getDuration());
        } else if (task instanceof Subtask) {
            csvTask = String.format("%d,SUBTASK,%s,%s,%s,%s,%s,%d",
                    task.getID(), task.getName(), task.getStatus(), task.getDescription(), task.getStartTime(),
                    task.getDuration(), ((Subtask) task).getEpicID());
        } else {
            csvTask = String.format("%d,TASK,%s,%s,%s,%s,%s",
                    task.getID(), task.getName(), task.getStatus(), task.getDescription(),
                    task.getStartTime(), task.getDuration());
        }
        return csvTask;
    }

    public static Task taskFromString(String task) {
        String[] split = task.split(",");

        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = LocalDateTime.parse(split[5]);
        Duration duration = Duration.parse(split[6]);

        switch (split[1]) {
            case "TASK":
                return new Task(name, description, status, id, startTime, duration);
            case "EPIC":
                return new Epic(name, description, id);
            case "SUBTASK":
                int epicID = Integer.parseInt(split[7]);
                return new Subtask(name, description, status, id, startTime, duration, epicID);
            default:
                throw new ManagerSaveException("Такой задачи не существует!");
        }
    }

    public static String historyToString(HistoryManager historyManager) {
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

    public static List<Integer> historyFromString(String history) {
        String[] split = history.split(",");
        List<Integer> result = new ArrayList<>();

        for (String part : split) {
            result.add(Integer.parseInt(part));
        }
        return result;
    }
}
