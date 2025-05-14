package task_tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;

import java.io.IOException;
import java.util.List;

import static task_tracker.server.HttpTaskServer.getIdFromQuery;
import static task_tracker.server.HttpTaskServer.sendText;

public class SubtasksOfEpicHandler extends ServerHandler implements HttpHandler {

    public SubtasksOfEpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            int id = getIdFromQuery(exchange);
            Epic epic = taskManager.getEpic(id);

            if (epic == null) {
                sendText(400, "Невозможно получить Эпик из исходных данных", exchange);
                return;
            }
            List<Subtask> subtasks = taskManager.getSubtasks(epic);

            sendText(200, gson.toJson(subtasks), exchange);
        } else {
            sendText(400, "Такой метод не поддерживается!", exchange);
        }
    }
}
