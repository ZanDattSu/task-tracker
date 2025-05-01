package task_tracker;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer idCounter = 1;

    private final Map<Integer, Task> tasks = new HashMap<>();

    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final Map<Integer, Epic> epics = new HashMap<>();

    private final Set<Task> prioritizedTasks;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>(taskTimeComparator);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void setHistory(List<Integer> history) {
        Map<Integer, Task> allTasks = new HashMap<>(tasks);
        allTasks.putAll(subtasks);
        allTasks.putAll(epics);

        for (Integer taskId : history) {
            Task task = allTasks.get(taskId);
            historyManager.add(task);
        }
    }

    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(getTasks());

        allTasks.addAll(getSubtasks());
        allTasks.addAll(getEpics());
        return allTasks;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
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
        Task newTask = new Task(
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                idCounter++,
                task.getStartTime(),
                task.getDuration()
        );
        if (!tasks.containsKey(newTask.getID())) {
            prioritizedTasks.add(newTask);
            validateTimeSlot();
            tasks.put(newTask.getID(), newTask);
            return newTask;
        } else {
            System.out.println("Задача с таким ID уже существует");
            return null;
        }
    }

    @Override
    public void updateTask(Task task) {
        Task originalTask = tasks.get(task.getID());
        prioritizedTasks.remove(originalTask);
        prioritizedTasks.add(task);
        validateTimeSlot();
        tasks.put(task.getID(), task);
    }

    @Override
    public Task removeTask(int id) {
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
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
            epic.calculateTime();
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
        Subtask newSubtask = new Subtask(
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus(),
                idCounter++,
                subtask.getStartTime(),
                subtask.getDuration(),
                epic.getID()
        );
        prioritizedTasks.add(newSubtask);
        validateTimeSlot();
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


            prioritizedTasks.remove(originalSubtask);
            prioritizedTasks.add(subtask);
            validateTimeSlot();
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
        epics.remove(id);
        return epic;
    }

    public void validateTimeSlot() {
        Task prev = null;
        for (Task curr : prioritizedTasks) {
            if (prev != null && !validateNonOverlap(prev, curr)) {
                throw new RuntimeException(
                        String.format("Задача %d (%s–%s) пересекается с %d (%s–%s)",
                                prev.getID(), prev.getStartTime(), prev.getEndTime(),
                                curr.getID(), curr.getStartTime(), curr.getEndTime())
                );
            }
            prev = curr;
        }
    }

    private boolean validateNonOverlap(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) {
            return true;
        }
        if (t1.getEndTime().isEqual(LocalDateTime.MIN) || t2.getEndTime().isEqual(LocalDateTime.MIN)){
            return true;
        }
        LocalDateTime s1 = t1.getStartTime(), e1 = t1.getEndTime();
        LocalDateTime s2 = t2.getStartTime(), e2 = t2.getEndTime();

        return e1.isBefore(s2) || e1.isEqual(s2) || e2.isBefore(s1) || e2.isEqual(s1);

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
