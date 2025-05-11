package task_tracker.server.handlers;

import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Epic;

import java.util.List;

public class EpicHandler extends AbstractTaskHandler<Epic> {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected List<Epic> getAllTasks() {
        return taskManager.getEpics();
    }

    @Override
    protected Epic getTask(int id) {
        return taskManager.getEpic(id);
    }

    @Override
    protected Epic addTask(Epic task) {
        return taskManager.addEpic(task);
    }

    @Override
    protected void clearTasks() {
        taskManager.clearEpics();
    }

    @Override
    protected Epic removeTask(int id) {
        return taskManager.removeEpic(id);
    }

    @Override
    protected String getTaskTypeName() {
        return "Эпик";
    }

    @Override
    protected Class<Epic> getTaskClass() {
        return Epic.class;
    }
}
