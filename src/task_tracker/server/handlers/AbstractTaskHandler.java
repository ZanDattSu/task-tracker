package task_tracker.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;

import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static task_tracker.server.HttpTaskServer.getIdFromQuery;
import static task_tracker.server.HttpTaskServer.sendText;

public abstract class AbstractTaskHandler<T> extends ServerHandler implements HttpHandler {

    public AbstractTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    protected abstract List<T> getAllTasks();

    protected abstract T getTask(int id);

    protected abstract T addTask(T task);

    protected abstract void clearTasks();

    protected abstract T removeTask(int id);

    protected abstract String getTaskTypeName();

    protected abstract Class<T> getTaskClass();

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendText(405, "Такой метод не поддерживается!", exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query == null) {
            List<T> tasks = getAllTasks();
            sendText(200, gson.toJson(tasks), exchange);
        } else {
            int id = getIdFromQuery(exchange);
            T item = getTask(id);

            if (item == null) {
                sendText(404, getTaskTypeName() + " с таким id не существует", exchange);
            } else {
                sendText(200, gson.toJson(item), exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        T item = gson.fromJson(body, getTaskClass());

        if (item == null) {
            sendText(400, "Невозможно получить " + getTaskTypeName()
                    + " из исходных данных", exchange);
            return;
        }
        addTask(item);

        sendText(201, getTaskTypeName() + " добавлен(а)", exchange);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query == null) {
            clearTasks();
            sendText(200, getTaskTypeName() + "(и) очищены", exchange);
        } else {
            int id = getIdFromQuery(exchange);
            T item = removeTask(id);

            if (item == null) {
                sendText(404, getTaskTypeName() + "(и) с id=" + id + " не существует", exchange);
            } else {
                sendText(200, gson.toJson(item), exchange);
            }
        }
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
}

