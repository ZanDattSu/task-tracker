package task_tracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;
import task_tracker.server.GsonProvider;
import task_tracker.tasks_type.Task;

import java.io.IOException;
import java.util.List;

import static task_tracker.server.HttpTaskServer.sendPlainText;
import static task_tracker.server.HttpTaskServer.sendResponse;

public class HistoryHandler implements HttpHandler {
    public static final String PLAIN_TEXT = "text/plain; charset=UTF-8";
    public static final String JSON = "application/json; charset=UTF-8";
    Gson gson = GsonProvider.getGson();
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            List<Task> history = taskManager.getHistory();

            String response = history.isEmpty() ? "История пустая" : gson.toJson(history);
            String contentType = history.isEmpty() ? PLAIN_TEXT : JSON;

            sendResponse(200, response, contentType, exchange);
        } else {
            sendPlainText(400, "Такой метод не поддерживается!", exchange);
        }
    }
}
