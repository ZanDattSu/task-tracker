package task_tracker.managers;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    HistoryManager getHistoryManager();

    //Получить все задачи, подзадачи и эпики
    List<Task> getAllTasks();

    // Методы для класса Task

    // Получение списка всех задач
    List<Task> getTasks();

    // Удаление всех задач
    void clearTasks();

    // Получение по Id
    Task getTask(int id);

    // Создание новой задачи
    Task addTask(Task task);

    // Обновление задачи
    void updateTask(Task task);

    // Удаление по Id
    Task removeTask(int id);


    // Методы для класса Subtask

    // Получение списка всех подзадач
    List<Subtask> getSubtasks();

    // Удаление всех подзадач
    void clearSubtasks();

    // Получение подзадачи по Id
    Subtask getSubtask(int id);

    // Создание новой подзадачи
    Subtask addSubtask(Subtask subtask);

    // Создание новой подзадачи
    Subtask addSubtask(Subtask subtask, Epic epic);

    // Обновление подзадачи
    void updateSubtask(Subtask subtask);

    // Удаление по подзадачи Id
    Subtask removeSubtask(int id);

    // Получение всех подзадач эпика
    List<Subtask> getSubtasks(Epic epic);


    // Методы для класса Epic

    // Получение списка всех эпиков
    List<Epic> getEpics();

    // Удаление всех эпиков
    void clearEpics();

    // Получение эпика по Id
    Epic getEpic(int id);

    // Создание нового эпика
    Epic addEpic(Epic epic);

    // Обновление эпика
    void updateEpic(Epic epic);

    // Удаление эпика по Id
    Epic removeEpic(int id);

    //Получить историю
    List<Task> getHistory();

    //Записать историю
    void setHistory(List<Integer> history);

    //Получить задачи по приоритету (время)
    Set<Task> getPrioritizedTasks();
}
