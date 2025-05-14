package task_tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Task;

import java.io.IOException;
import java.util.List;

import static task_tracker.server.HttpTaskServer.sendText;

public class HistoryHandler extends ServerHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            List<Task> history = taskManager.getHistory();

            String response = history.isEmpty() ? "История пустая" : gson.toJson(history);

            sendText(200, response, exchange);
        } else {
            sendText(400, "Такой метод не поддерживается!", exchange);
        }
    }
}
