package task_tracker;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final File file = new File("status.csv");

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
        try (Writer fileWriter = new FileWriter(file.getName())) {
            ArrayList<Task> allTasks = new ArrayList<>(getAllTasks());
            allTasks.addAll(getAllSubtasks());
            allTasks.addAll(getAllEpics());

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
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
            boolean areTasksOver = false;

            while (br.ready()) {
                String[] s = br.readLine().split(",");

                if (s[0].equals("id")) {
                    continue;
                }
                if (!s[0].isEmpty() && !areTasksOver) {
                    Task task = Parser.taskFromString(s);
                    if (task instanceof Epic){
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

    public static void main(String[] args) {

//        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager();
        FileBackedTaskManager fileBackedTasksManager = loadFromFile(file);


/*        // Проверяем создание и вывод задач
        Task task1 = fileBackedTasksManager.addTask(new Task("task 1", "desc task 1"));
        Task task2 = fileBackedTasksManager.addTask(new Task("task 2", "desc task 2"));
        System.out.println("Выводим все задачи" + "\n" + fileBackedTasksManager.getAllTasks());

        // Проверяем создание и вывод эпиков
        Epic epic1 = fileBackedTasksManager.addEpic(new Epic("epic 1", "desc epic 1"));
        Epic epic2 = fileBackedTasksManager.addEpic(new Epic("epic 2", "desc epic 2"));
        System.out.println("Выводим все эпики" + "\n" + fileBackedTasksManager.getAllEpics());

        // Проверяем создание и вывод подзадач
        Subtask subtask1 = fileBackedTasksManager.addSubtask(
                new Subtask("subTask 1", "desc subTask 1", Status.NEW, epic1.getID()), epic1);
        Subtask subtask2 = fileBackedTasksManager.addSubtask(
                new Subtask("subTask 2", "desc subTask 2", Status.NEW, epic2.getID()), epic1);
        Subtask subtask3 = fileBackedTasksManager.addSubtask(
                new Subtask("subTask 3", "desc subTask 3", Status.NEW, epic1.getID()), epic1);
        System.out.println("Выводим подзадачи эпика 1" + "\n" + fileBackedTasksManager.getSubtasks(epic1));

        System.out.println("Выводим пустую историю" + "\n" + fileBackedTasksManager.getHistory());

        // Проверяем историю.
        fileBackedTasksManager.getTask(task1.getID());
        fileBackedTasksManager.getEpic(epic1.getID());
        fileBackedTasksManager.getEpic(epic2.getID());
        fileBackedTasksManager.getEpic(epic1.getID());
        fileBackedTasksManager.getEpic(epic2.getID());*/

        System.out.println("Выводим все задачи" + "\n" + fileBackedTasksManager.getAllTasks());
        System.out.println("Выводим все эпики" + "\n" + fileBackedTasksManager.getAllEpics());
        System.out.println("Выводим все подзадачи" + "\n" + fileBackedTasksManager.getAllSubtasks());

        System.out.println("Выводим историю" + "\n" + fileBackedTasksManager.getHistory());

    }
}
