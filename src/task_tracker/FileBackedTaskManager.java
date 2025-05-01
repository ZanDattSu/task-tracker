package task_tracker;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file){
        this.file = file;
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
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
    public Epic removeEpic(int id) {
        Epic removedEpic = super.removeEpic(id);
        save();
        return removedEpic;
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            List<Task> allTasks = getAllTasks();

            allTasks.sort((o1, o2) -> o1.getID() - o2.getID());

            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : allTasks) {
                fileWriter.write(task.toCsvString() + "\n");
            }

            fileWriter.write("\n" + Parser.historyToCsvString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файлов!", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        if (!file.exists()) {
            return taskManager;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
            boolean areTasksOver = false;

            while (br.ready()) {
                String[] s = br.readLine().split(",");

                if (s[0].equals("id")) {
                    continue;
                }
                if (!s[0].isEmpty() && !areTasksOver) {
                    Task task = Parser.taskFromString(s);
                    if (task instanceof Epic) {
                        taskManager.addEpic((Epic) task);
                    } else if (task instanceof Subtask) {
                        taskManager.addSubtask((Subtask) task);
                    } else {
                        taskManager.addTask(task);
                    }
                } else if (!areTasksOver) {
                    areTasksOver = true;
                } else {
                    List<Integer> history = Parser.historyFromString(s);
                    taskManager.setHistory(history);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файлов!", e);
        }
        return taskManager;
    }
}