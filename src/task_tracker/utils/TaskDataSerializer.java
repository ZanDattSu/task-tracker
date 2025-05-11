package task_tracker.utils;

import task_tracker.managers.HistoryManager;
import task_tracker.managers.ManagerSaveException;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.io.*;
import java.util.Comparator;
import java.util.List;

public class TaskDataSerializer {

    public static void saveToCsv(File file, List<Task> tasks, HistoryManager history) {
        try (Writer fileWriter = new FileWriter(file)) {
            tasks.sort(Comparator.comparingInt(Task::getID));

            fileWriter.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Task task : tasks) {
                fileWriter.write(CsvParser.taskToString(task) + "\n");
            }

            fileWriter.write("\n" + CsvParser.historyToString(history));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл: " + file.getPath(), e);
        }
    }

    public static void loadIntoManager(File file, TaskManager manager) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            boolean areTasksOver = false;

            while (br.ready()) {
                String s = br.readLine();

                if (s.equals("id,type,name,status,description,startTime,duration,epic")) {
                    continue;
                }
                if (!s.isEmpty() && !areTasksOver) {
                    Task task = CsvParser.taskFromString(s);
                    addTaskToManager(task, manager);
                } else if (!areTasksOver) {
                    areTasksOver = true;
                } else {
                    List<Integer> history = CsvParser.historyFromString(s);
                    manager.setHistory(history);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файлов!", e);
        }
    }

    private static void addTaskToManager(Task task, TaskManager manager) {
        if (task instanceof Epic) {
            manager.addEpic((Epic) task);
        } else if (task instanceof Subtask) {
            manager.addSubtask((Subtask) task);
        } else {
            manager.addTask(task);
        }
    }
}
