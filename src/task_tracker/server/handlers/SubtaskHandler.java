package task_tracker.server.handlers;

import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Subtask;

import java.util.List;

public class SubtaskHandler extends AbstractTaskHandler<Subtask> {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected List<Subtask> getAllTasks() {
        return taskManager.getSubtasks();
    }

    @Override
    protected Subtask getTask(int id) {
        return taskManager.getSubtask(id);
    }

    @Override
    protected Subtask addTask(Subtask task) {
        return taskManager.addSubtask(task);
    }

    @Override
    protected void clearTasks() {
        taskManager.clearSubtasks();
    }

    @Override
    protected Subtask removeTask(int id) {
        return taskManager.removeSubtask(id);
    }

    @Override
    protected String getTaskTypeName() {
        return "Подзадача";
    }

    @Override
    protected Class<Subtask> getTaskClass() {
        return Subtask.class;
    }
}
