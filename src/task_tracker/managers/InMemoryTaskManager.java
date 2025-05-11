package task_tracker.managers;

import task_tracker.utils.TaskTimeValidator;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer idCounter = 1;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Subtask> subtasks;
    private final Map<Integer, Epic> epics;
    private final Set<Task> prioritizedTasks;
    private final HistoryManager historyManager;
    private final TaskTimeValidator timeValidator;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(taskTimeComparator);
        this.historyManager = Managers.getDefaultHistory();
        this.timeValidator = new TaskTimeValidator();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(getTasks());
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
        prioritizedTasks.removeIf(t -> t.getClass().equals(Task.class));
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
        Task newTask;
        if (task.getID() == Task.getDefaultId()) {
            newTask = new Task(
                    task.getName(),
                    task.getDescription(),
                    task.getStatus(),
                    idCounter++,
                    task.getStartTime(),
                    task.getDuration()
            );
        } else {
            newTask = new Task(
                    task.getName(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getID(),
                    task.getStartTime(),
                    task.getDuration()
            );
            idCounter = Math.max(idCounter, task.getID() + 1);
        }

        if (tasks.containsKey(newTask.getID())) {
            this.updateTask(newTask);
        } else {
            prioritizedTasks.add(newTask);
            timeValidator.validateTimeSlot(prioritizedTasks);
            tasks.put(newTask.getID(), newTask);
        }

        return newTask;

    }

    @Override
    public void updateTask(Task task) {
        Task originalTask = tasks.get(task.getID());
        if (originalTask == null) {
            return;
        }

        prioritizedTasks.remove(originalTask);
        prioritizedTasks.add(task);
        timeValidator.validateTimeSlot(prioritizedTasks);
        tasks.put(task.getID(), task);
    }

    @Override
    public Task removeTask(int id) {
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        historyManager.remove(id);
        prioritizedTasks.remove(task);
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
            epic.updateTimeParameters();
        }
        prioritizedTasks.removeIf(task -> task.getClass().equals(Subtask.class));
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
        return addSubtask(subtask, epic);
    }

    @Override
    public Subtask addSubtask(Subtask subtask, Epic epic) {
        if (epic == null) {
            System.out.println("Эпик не найден");
            return null;
        }
        Subtask newSubtask;
        if (subtask.getID() == Subtask.getDefaultId()) {
            newSubtask = new Subtask(
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    idCounter++,
                    subtask.getStartTime(),
                    subtask.getDuration(),
                    epic.getID()
            );
        } else {
            newSubtask = new Subtask(
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getID(),
                    subtask.getStartTime(),
                    subtask.getDuration(),
                    epic.getID()
            );
            idCounter = Math.max(idCounter, subtask.getID() + 1);
        }

        if (subtasks.containsKey(newSubtask.getID())) {
            this.updateSubtask(newSubtask);
        } else {
            prioritizedTasks.add(newSubtask);
            timeValidator.validateTimeSlot(prioritizedTasks);
            subtasks.put(newSubtask.getID(), newSubtask);

            epic.addSubtask(newSubtask);
            updateEpicStatus(epic);
        }

        return newSubtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask originalSubtask = subtasks.get(subtask.getID());
        if (originalSubtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicID());
        if (epic == null) {
            return;
        }
        prioritizedTasks.remove(originalSubtask);
        prioritizedTasks.add(subtask);
        timeValidator.validateTimeSlot(prioritizedTasks);
        subtasks.put(subtask.getID(), subtask);

        epic.removeSubtask(originalSubtask);
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
    }

    @Override
    public Subtask removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpicID());
        if (epic != null) {
            epic.removeSubtask(subtask);
            updateEpicStatus(epic);
        }

        historyManager.remove(id);
        prioritizedTasks.remove(subtask);
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
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task.getClass().equals(Subtask.class));
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
        Epic newEpic;
        if (epic.getID() == Epic.getDefaultId()) {
            newEpic = new Epic(
                    epic.getName(),
                    epic.getDescription(),
                    idCounter++
            );
        } else {
            newEpic = new Epic(
                    epic.getName(),
                    epic.getDescription(),
                    epic.getID()
            );
            idCounter = Math.max(idCounter, epic.getID() + 1);
        }
        if (epics.containsKey(newEpic.getID())) {
            this.updateEpic(newEpic);
        } else {
            epics.put(newEpic.getID(), newEpic);
        }
        List<Subtask> subtasks = epic.getSubtasks();
        if (subtasks != null) {
            for (Subtask subtask : subtasks) {
                addSubtask(subtask, newEpic);
            }
        }
        return newEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getID())) {
            return;
        }
        epics.put(epic.getID(), epic);
    }

    public Epic removeEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return null;
        }

        List<Integer> subtaskIDs = new ArrayList<>(epic.getSubtasksID());
        for (Integer subID : subtaskIDs) {
            removeSubtask(subID);
        }

        historyManager.remove(id);
        return epics.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void setHistory(List<Integer> history) {
        Map<Integer, Task> allTasks = new HashMap<>(tasks);
        allTasks.putAll(subtasks);
        allTasks.putAll(epics);

        for (Integer taskId : history) {
            Task task = allTasks.get(taskId);
            historyManager.add(task);
        }
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private static final Comparator<Task> taskTimeComparator = (o1, o2) -> {
        LocalDateTime s1 = o1.getStartTime();
        LocalDateTime s2 = o2.getStartTime();

        if (s1 == null && s2 == null) {
            return Integer.compare(o1.getID(), o2.getID());
        }
        if (s1 == null) {
            return 1;
        }
        if (s2 == null) {
            return -1;
        }
        int cmp = s1.compareTo(s2);
        return (cmp != 0) ? cmp : Integer.compare(o1.getID(), o2.getID());
    };
}
