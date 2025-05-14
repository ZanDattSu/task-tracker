package task_tracker.server.handlers;

import com.google.gson.Gson;
import task_tracker.managers.TaskManager;
import task_tracker.server.GsonProvider;

abstract class ServerHandler {
    protected final Gson gson = GsonProvider.getGson();
    protected final TaskManager taskManager;

    public ServerHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
}
