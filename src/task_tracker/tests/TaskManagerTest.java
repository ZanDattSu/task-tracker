package task_tracker.tests;

import org.junit.jupiter.api.Test;
import task_tracker.TaskManager;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task exampleTask;
    protected Epic exampleEpic;
    protected Subtask earlySubt;
    protected Subtask laterSubt;
    protected LocalDateTime exampleTaskStart;
    protected Duration exampleTaskDuration;
    protected LocalDateTime earlySubtStart;
    protected Duration earlySubtDuration;
    protected LocalDateTime laterSubtStart;
    protected Duration laterSubtDuration;

    void setup() {
        exampleTaskStart = LocalDateTime.of(2026, 1, 1, 12, 0);
        exampleTaskDuration = Duration.ofMinutes(90);

        exampleTask = taskManager.addTask(new Task("Task 1", "Task Desc 1",
                Status.NEW, exampleTaskStart, exampleTaskDuration));
        exampleEpic = taskManager.addEpic(new Epic("Epic 1", "Epic Desc 1"));

        earlySubtStart = LocalDateTime.of(2025, 3, 3, 9, 0);
        earlySubtDuration = Duration.ofMinutes(30);
        laterSubtStart = LocalDateTime.of(2025, 3, 3, 10, 0);
        laterSubtDuration = Duration.ofMinutes(60);

        earlySubt = taskManager.addSubtask((new Subtask("Sub 1", "Sub Desc 1",
                Status.NEW, earlySubtStart, earlySubtDuration, exampleEpic.getID())), exampleEpic);
        laterSubt = taskManager.addSubtask(
                new Subtask("Sub 2", "Sub Desc 2",
                        Status.NEW, laterSubtStart, laterSubtDuration, exampleEpic.getID()));
    }

    @Test
    void getTasks() {
        assertNotNull(taskManager.getTasks(), "Задачи на возвращаются.");
    }

    @Test
    void getEpics() {
        assertNotNull(taskManager.getEpics(), "Эпики на возвращаются.");
    }

    @Test
    void getSubtasks() {
        List<Subtask> subTasks = taskManager.getSubtasks(exampleEpic);
        assertNotNull(subTasks, "Подзадачи на возвращаются.");
    }

    @Test
    void clearTasks() {
        taskManager.addTask(new Task("TaskName", "TaskDescription"));
        taskManager.clearTasks();
        assertEquals(0, taskManager.getTasks().size(), "Задачи не удаляются");
    }

    @Test
    void clearSubtasks() {
        taskManager.addSubtask(new Subtask("SubtaskName", "SubtaskDescription", exampleEpic.getID()));
        taskManager.clearSubtasks();
        assertEquals(0, taskManager.getSubtasks().size(), "Задачи не удаляются");
    }

    @Test
    void clearEpics() {
        taskManager.addTask(new Epic("EpicName", "EpicDescription"));
        taskManager.clearEpics();
        assertEquals(0, taskManager.getEpics().size(), "Задачи не удаляются");
    }

    @Test
    void getSubtask() {
        Subtask subTask = taskManager.getSubtask(earlySubt.getID());
        Subtask subTaskErr = taskManager.getSubtask(10001);
        assertEquals(3, subTask.getID(), "ID не совпадают");
        assertNull(subTaskErr, "Задача существует");
    }

    @Test
    void getTask() {
        Task task = taskManager.getTask(exampleTask.getID());
        Task taskErr = taskManager.getTask(10001);
        assertEquals(1, task.getID(), "ID не совпадают");
        assertNull(taskErr, "Задача существует");
    }

    @Test
    void getEpic() {
        Epic epic1 = taskManager.getEpic(exampleEpic.getID());
        Epic epicErr = taskManager.getEpic(10001);
        assertEquals(exampleEpic.getID(), epic1.getID(), "ID не совпадают");
        assertNull(epicErr, "Задача существует");
    }

    @Test
    void addTask() {
        final Task savedTask = taskManager.getTask(exampleTask.getID());
        assertNotNull(savedTask, "Задача не найдена.");
        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
    }

    @Test
    void addSubtask() {
        Subtask subTask = taskManager.getSubtask(earlySubt.getID());
        assertNotNull(subTask, "Подзадача не найдена");
    }

    @Test
    void addEpic() {
        Epic epic1 = taskManager.getEpic(exampleEpic.getID());
        assertNotNull(epic1, "Эпик не найден");
    }

    @Test
    void updateTask() {
        Task task = taskManager.addTask(new Task("TaskName", "TaskDescription"));
        Task newTask = new Task("New taskName", "New taskDescription", task.getID());

        taskManager.updateTask(newTask);
        Task currentTask = taskManager.getTask(newTask.getID());

        assertEquals(newTask, currentTask, "Задача не обновилась");
    }

    @Test
    void updateSubtask() {
        Subtask subTask = taskManager.addSubtask((new Subtask("SubTaskName",
                "SubTaskDescription", Status.IN_PROGRESS, exampleEpic.getID())), exampleEpic);
        Subtask newSubtask = (new Subtask("new SubTaskName", "new SubTaskDescription",
                Status.IN_PROGRESS, subTask.getID(), exampleEpic.getID()));

        taskManager.updateSubtask(newSubtask);
        Subtask currentSubtask = taskManager.getSubtask(newSubtask.getID());

        assertEquals(newSubtask, currentSubtask, "Задача не обновилась");
    }

    @Test
    void updateEpic() {
        Epic epic = taskManager.addEpic(new Epic("EpicName", "EpicDescription"));
        Epic newEpic = (new Epic("new EpicName", "new EpicDescription", epic.getID()));

        taskManager.updateEpic(newEpic);
        Epic currentEpic = taskManager.getEpic(newEpic.getID());

        assertEquals(newEpic, currentEpic, "Эпик не обновился");
    }

    @Test
    void removeFromHistoryById() {
        taskManager.getTask(exampleTask.getID());
        taskManager.getTask(exampleTask.getID());
        taskManager.getEpic(earlySubt.getID());
        taskManager.getSubtask(exampleEpic.getID());

        List<Task> tasksHistory = taskManager.getHistory();
        taskManager.getHistoryManager().remove(exampleTask.getID());

        List<Task> history = taskManager.getHistory();

        assertNotEquals(tasksHistory.size(), history.size(), "История не изменилась");
    }

    @Test
    void getHistory() {
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не получена или пустая");
    }

    @Test
    void addInHistory() {
        List<Task> history = taskManager.getHistory();
        Task task1 = taskManager.addTask(new Task("TaskName2", "TaskDescription"));
        taskManager.getTask(exampleTask.getID());

        taskManager.getHistoryManager().add(task1);
        List<Task> tasksHist1 = taskManager.getHistory();

        assertTrue(history.size() != tasksHist1.size(), "История не изменилась");
    }

    @Test
    void removeTask() {
        int taskId = exampleTask.getID();
        int before = taskManager.getTasks().size();

        taskManager.removeTask(taskId);
        assertEquals(before - 1, taskManager.getTasks().size(), "Задача не удалена из списка");
        assertNull(taskManager.getTask(taskId), "Удалённая задача всё ещё доступна");
    }

    @Test
    void removeSubtask() {
        int subtaskID = earlySubt.getID();
        int beforeSubCount = taskManager.getSubtasks().size();

        taskManager.removeSubtask(subtaskID);

        assertEquals(beforeSubCount - 1, taskManager.getSubtasks().size(),
                "Подзадача не удалена из эпика");
        assertNull(taskManager.getSubtask(subtaskID), "Удалённая подзадача всё ещё доступна");
    }

    @Test
    void removeEpic() {
        int epicId = exampleEpic.getID();
        int beforeEpicCount = taskManager.getEpics().size();
        List<Subtask> subtasks = taskManager.getSubtasks(exampleEpic);

        taskManager.removeEpic(epicId);

        assertEquals(beforeEpicCount - 1, taskManager.getEpics().size(), "Эпик не удалён");
        assertNull(taskManager.getEpic(epicId), "Удалённый эпик всё ещё доступен");
        for (Subtask st : subtasks) {
            assertNull(taskManager.getSubtask(st.getID()),
                    "Подзадача эпика не удалена: id=" + st.getID());
        }
    }

    @Test
    void taskShouldStoreStartTimeAndDuration() {
        assertEquals(exampleTaskStart, exampleTask.getStartTime(), "Start time should be set correctly");
        assertEquals(exampleTaskDuration, exampleTask.getDuration(), "Duration should be set correctly");
        assertEquals(exampleTaskStart.plus(exampleTaskDuration), exampleTask.getEndTime(),
                "End time should be correctly calculated");
    }

    @Test
    void subtaskShouldStoreStartTimeAndDuration() {
        assertEquals(earlySubtStart, earlySubt.getStartTime());
        assertEquals(earlySubtDuration, earlySubt.getDuration());
        assertEquals(earlySubtStart.plus(earlySubtDuration), earlySubt.getEndTime());
    }

    @Test
    void epicShouldCalculateStartTimeAndDurationFromSubtasks() {
        assertEquals(earlySubtStart, exampleEpic.getStartTime(),
                "Epic start time should be the earliest subtask start time");
        assertEquals(Duration.ofMinutes(120), exampleEpic.getDuration(),
                "Epic duration should be sum from earliest start to latest end");
        assertEquals(laterSubtStart.plus(laterSubtDuration), exampleEpic.getEndTime(),
                "Epic end time should match latest subtask end time");
    }

    @Test
    void getPrioritizedTasksAfterUpdateReflectsNewOrder() {
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        Iterator<Task> it1 = taskManager.getPrioritizedTasks().iterator();
        assertSameById(earlySubt, it1.next());
        assertSameById(laterSubt, it1.next());

        Subtask theEarliest = taskManager.addSubtask((new Subtask("theEarliest", "",
                Status.NEW, earlySubtStart.minusDays(1), earlySubtDuration, exampleEpic.getID())), exampleEpic);

        Iterator<Task> it2 = taskManager.getPrioritizedTasks().iterator();
        assertSameById(theEarliest, it2.next());
        assertSameById(earlySubt, it2.next());
    }

    private void assertSameById(Task expected, Task actual) {
        assertNotNull(actual, "Задача не должна быть null");
        assertEquals(expected.getID(), actual.getID(),
                "Ожидается задача с ID=" + expected.getID());
    }

}