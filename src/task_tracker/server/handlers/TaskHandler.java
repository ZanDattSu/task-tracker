package task_tracker.server.handlers;

import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Task;

import java.util.List;

public class TaskHandler extends AbstractTaskHandler<Task> {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected List<Task> getAllTasks() {
        return taskManager.getTasks();
    }

    @Override
    protected Task getTask(int id) {
        return taskManager.getTask(id);
    }

    @Override
    protected Task addTask(Task task) {
        return taskManager.addTask(task);
    }

    @Override
    protected void clearTasks() {
        taskManager.clearTasks();
    }

    @Override
    protected Task removeTask(int id) {
        return taskManager.removeTask(id);
    }

    @Override
    protected String getTaskTypeName() {
        return "Задача";
    }

    @Override
    protected Class<Task> getTaskClass() {
        return Task.class;
    }
}
