package task_tracker.server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;
import task_tracker.server.GsonProvider;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;

import java.io.IOException;
import java.util.List;

import static task_tracker.server.HttpTaskServer.*;

public class SubtasksOfEpicHandler implements HttpHandler {
    Gson gson = GsonProvider.getGson();
    private final TaskManager taskManager;

    public SubtasksOfEpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            int id = getIdFromQuery(exchange.getRequestURI().getQuery(), exchange);
            Epic epic = taskManager.getEpic(id);

            if (epic == null) {
                sendPlainText(400, "Невозможно получить Эпик из исходных данных", exchange);
                return;
            }
            List<Subtask> subtasks = taskManager.getSubtasks(epic);

            sendJson(200, gson.toJson(subtasks), exchange);
        } else {
            sendPlainText(400, "Такой метод не поддерживается!", exchange);
        }
    }
}
