package task_tracker;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private Integer idCounter = 1;

    private final HashMap<Integer, Task> tasks = new HashMap<>();

    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void setHistory(List<Integer> history) {
        HashMap<Integer, Task> allTasks = new HashMap<>(tasks);
        allTasks.putAll(subtasks);
        allTasks.putAll(epics);

        for (Integer taskId : history) {
            Task task = allTasks.get(taskId);
            historyManager.add(task);
        }
    }

    public List<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(getTasks());

        allTasks.addAll(getSubtasks());
        allTasks.addAll(getEpics());
        return allTasks;
    }

    // Методы для класса Task
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
            Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = new Task(
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                idCounter++
        );

        if (!tasks.containsKey(newTask.getID())) {
            tasks.put(newTask.getID(), newTask);
            return newTask;
        } else {
            System.out.println("Задача с таким ID уже существует");
            return null;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getID())) {
            tasks.put(task.getID(), task);
        }
    }

    @Override
    public Task removeTask(int id) {
        historyManager.remove(id);
        return tasks.remove(id);
    }


    // Методы для класса Subtask

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicID());
        if (epic == null) {
            System.out.println("Эпик не найден");
            return null;
        }
        Subtask newSubtask = new Subtask(
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus(),
                idCounter++,
                epic.getID()
        );
        subtasks.put(newSubtask.getID(), newSubtask);
        epic.addSubtask(newSubtask);
        updateEpicStatus(epic);

        return newSubtask;
    }

    @Override
    public Subtask addSubtask(Subtask subtask, Epic epic) {
        Subtask newSubtask = new Subtask(
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus(),
                idCounter++,
                epic.getID()
        );
        subtasks.put(newSubtask.getID(), newSubtask);
        epic.addSubtask(newSubtask);
        updateEpicStatus(epic);

        return newSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask originalSubtask = subtasks.get(subtask.getID());
        if (subtasks.containsKey(subtask.getID())) {
            Epic epic = epics.get(subtask.getEpicID());

            subtasks.put(subtask.getID(), subtask);
            epic.removeSubtask(originalSubtask);
            epic.addSubtask(subtask);
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicID());
            epic.removeSubtask(subtask);
            updateEpicStatus(epic);
        }
        historyManager.remove(id);
        return subtasks.remove(id);
    }

    @Override
    public List<Subtask> getSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        int newCount = 0;
        int doneCount = 0;

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == Status.NEW) {
                newCount++;
            }
            if (subtask.getStatus() == Status.DONE) {
                doneCount++;
            }
        }
        if (newCount == epic.getSubtasks().size()) {
            epic.setStatus(Status.NEW);
        } else if (doneCount == epic.getSubtasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    // Методы для класса Epic

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearEpics() {
        epics.clear();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = new Epic(
                epic.getName(),
                epic.getDescription(),
                idCounter++
        );
        if (!epics.containsKey(newEpic.getID())) {
            epics.put(newEpic.getID(), newEpic);
            return newEpic;
        } else {
            System.out.println("Эпик с таким ID уже существует");
            return null;
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getID())) {
            epics.put(epic.getID(), epic);
        }
    }

    @Override
    public Epic removeEpic(int id) {
        Epic epic = epics.get(id);
        for (Subtask subtask : epic.getSubtasks()) {
            removeSubtask(subtask.getID());
            historyManager.remove(subtask.getID());
        }
        historyManager.remove(id);
        return epics.remove(id);
    }
}
