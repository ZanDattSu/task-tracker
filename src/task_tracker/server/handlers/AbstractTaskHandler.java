package task_tracker.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task_tracker.managers.TaskManager;
import task_tracker.server.GsonProvider;
import task_tracker.server.HttpTaskServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static task_tracker.server.HttpTaskServer.sendJson;
import static task_tracker.server.HttpTaskServer.sendPlainText;

public abstract class AbstractTaskHandler<T> implements HttpHandler {
    Gson gson = GsonProvider.getGson();
    protected final TaskManager taskManager;

    public AbstractTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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
        String query = exchange.getRequestURI().getQuery();

        switch (method) {
            case "GET":
                handleGet(exchange, query);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "DELETE":
                handleDelete(exchange, query);
                break;
            default:
                sendPlainText(400, "Такой метод не поддерживается!", exchange);
        }
    }

    private void handleGet(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            List<T> tasks = getAllTasks();
            sendJson(200, gson.toJson(tasks), exchange);
        } else {
            Optional<Integer> maybeId = getIdFromQuery(query, exchange);
            if (maybeId.isEmpty()) {
                return;
            }
            int id = maybeId.get();

            T item = getTask(id);
            if (item == null) {
                sendPlainText(404, getTaskTypeName() + " с таким id не существует", exchange);
            } else {
                sendJson(200, gson.toJson(item), exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            T item = gson.fromJson(body, getTaskClass());
            if (item == null) {
                sendPlainText(400, "Невозможно получить " + getTaskTypeName()
                        + " из исходных данных", exchange);
                return;
            }
            addTask(item);

            sendPlainText(201, getTaskTypeName() + " добавлен(а)", exchange);
        } catch (JsonSyntaxException e) {
            sendPlainText(400, "Неверный JSON: " + e.getMessage(), exchange);
        } catch (Exception e) {
            sendPlainText(500, "Ошибка сервера: " + e.getMessage(), exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String query) throws IOException {
        if (query == null) {
            clearTasks();
            sendPlainText(200, getTaskTypeName() + "(и) очищены", exchange);
        } else {
            Optional<Integer> maybeId = getIdFromQuery(query, exchange);
            if (maybeId.isEmpty()) {
                return;
            }
            int id = maybeId.get();

            T item = removeTask(id);
            if (item == null) {
                sendPlainText(404, getTaskTypeName() + "(и) с id=" + id + " не существует", exchange);
            } else {
                sendJson(200, gson.toJson(item), exchange);
            }
        }
    }

    private Optional<Integer> getIdFromQuery(String query, HttpExchange exchange) throws IOException {
        Map<String, String> params = HttpTaskServer.parseQuery(query);
        if (!params.containsKey("id")) {
            sendPlainText(400, "Ожидался параметр 'id'", exchange);
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(params.get("id")));
        } catch (NumberFormatException e) {
            sendPlainText(400, "Некорректный id: " + params.get("id"), exchange);
            return Optional.empty();
        }
    }
}

