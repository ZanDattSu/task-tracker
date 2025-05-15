package task_tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Task;

import java.io.IOException;
import java.util.Set;

import static task_tracker.server.HttpTaskServer.sendText;

public class PrioritizedHandler extends ServerHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            sendText(200, gson.toJson(prioritizedTasks), exchange);
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            taskManager.clearTasks();
            taskManager.clearEpics();
            sendText(200, "Все задачи удалены", exchange);
        } else {
            sendText(405, "Такой метод не поддерживается!", exchange);
        }
    }
}
