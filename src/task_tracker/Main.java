package task_tracker;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = taskManager.getHistoryManager();

        Task t1 = new Task("Go to VSU", -1);
        Task t2 = new Task("Drink Tea", -1);
        taskManager.addTask(t1);
        taskManager.addTask(t2);

        Epic e1 = new Epic("Do project", -1);
        Epic e2 = new Epic("Reach GM", 0);
        taskManager.addEpic(e1);
        taskManager.addEpic(e2);

        Subtask s1 = new Subtask("Learn Java", -1, 0);
        Subtask s2 = new Subtask("Programming", -1, 0);
        Subtask s3 = new Subtask("Practice on tracer", -1, 1);
        s1 = taskManager.addSubtask(s1);
        s2 = taskManager.addSubtask(s2);

        s3 = taskManager.addSubtask(s3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("\n" + "--------------------");

        s3.setStatus(Status.DONE);
        taskManager.updateSubtask(s3);

        s1.setStatus(Status.IN_PROGRESS);
        s2.setStatus(Status.DONE);
        taskManager.updateSubtask(s1);
        taskManager.updateSubtask(s2);

        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\n" + "--------------------");

        Epic epic1 = taskManager.getEpic(1);
        Epic epic2 = taskManager.getEpic(0);

        Subtask subtask1 = taskManager.getSubtask(0);
        Subtask subtask2 = taskManager.getSubtask(1);

        Task task1 = taskManager.getTask(0);
        Task task2 = taskManager.getTask(1);
        task2 = taskManager.getTask(1);
        task2 = taskManager.getTask(1);
        task2 = taskManager.getTask(1);
        task2 = taskManager.getTask(1);
        task2 = taskManager.getTask(0);

        if (historyManager.getHistory().size() == 10) {
            System.out.println("история работает. Количество задач: " +  historyManager.getHistory().size());
        } else {
            System.out.println("история швах. Количество задач: " +  historyManager.getHistory().size());
        }
        System.out.println("печатаем историю эпиков");
        List<Task> epics = historyManager.getHistory();
        for (var e: epics) {
            System.out.println(e);
        }

        System.out.println("\n" + "--------------------");
        
    }
}