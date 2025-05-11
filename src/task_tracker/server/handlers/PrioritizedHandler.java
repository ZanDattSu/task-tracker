package task_tracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;
import task_tracker.server.GsonProvider;
import task_tracker.tasks_type.Task;

import java.io.IOException;
import java.util.Set;

import static task_tracker.server.HttpTaskServer.*;

public class PrioritizedHandler implements HttpHandler {
    Gson gson = GsonProvider.getGson();
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            sendJson(200, gson.toJson(prioritizedTasks), exchange);
        } else {
            sendPlainText(400, "Такой метод не поддерживается!", exchange);
        }
    }
}
