package task_tracker.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import task_tracker.managers.FileBackedTaskManager;
import task_tracker.managers.Managers;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpTaskServer {
    private static final int PORT = 8082;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final File file = new File("http-task-server-status.csv");
    private static final FileBackedTaskManager taskManager = Managers.getFileBacked(file);
    private final HttpServer server;
    private static final Gson gson = GsonProvider.getGson();


    public HttpTaskServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера");
            throw new RuntimeException(e);
        }
    }

    public void start() {
        server.createContext("/tasks", new PrioritizedHandler());
        server.createContext("/tasks/task", new TaskHandler());
        server.createContext("/tasks/subtask", new SubtaskHandler());
        server.createContext("/tasks/epic", new EpicHandler());
        server.createContext("/tasks/history", new HistoryHandler());

        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    private static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            switch (method) {
                case "GET": {
                    if (query == null) {
                        List<Task> tasks = taskManager.getTasks();
                        String response = gson.toJson(tasks);

                        exchange.getResponseHeaders().set("Content-Type", "applications/json");
                        sendResponseHeaders(200, response, exchange);
                        sendResponseBody(response, exchange);
                    } else {
                        Map<String, String> params = parseQuery(query);
                        if (params.containsKey("id")) {
                            int id = Integer.parseInt(params.get("id"));
                            Task task = taskManager.getTask(id);

                            if (task == null) {
                                sendResponse(404, "Задачи с таким id не существует",
                                        "text/plain; charset=utf-8", exchange);
                            } else {
                                sendResponse(200, gson.toJson(task), "application/json; charset=utf-8", exchange);
                            }
                        } else {
                            sendResponse(400, "Такого параметра не существует!",
                                    "text/plain; charset=utf-8", exchange);
                        }
                    }
                    break;
                }
                case "POST": {
                    String response;
                    try (InputStream is = exchange.getRequestBody()) {
                        String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                        Task task = gson.fromJson(body, Task.class);
                        taskManager.addTask(task);

                        boolean taskAlreadyExist = taskManager.getTasks().stream()
                                .anyMatch(existingTask -> existingTask.getID() == task.getID());
                        response = taskAlreadyExist ? "Задача обновлена" : "Задача добавлена";
                    } catch (Exception e) {
                        response = "Ошибка при обработке запроса: " + e.getMessage();
                        sendResponseHeaders(400, response, exchange);
                        sendResponseBody(response, exchange);
                    }
                    sendResponseHeaders(201, response, exchange);
                    sendResponseBody(response, exchange);
                    break;
                }
                case "DELETE": {
                    if (query == null) {
                        taskManager.clearTasks();
                        String response = "Задачи очищены";
                        sendResponseHeaders(200, response, exchange);
                        sendResponseBody(response, exchange);
                    } else {
                        Map<String, String> params = parseQuery(query);
                        if (params.containsKey("id")) {
                            int id = Integer.parseInt(params.get("id"));
                            Task task = taskManager.removeTask(id);
                            String response = gson.toJson(task);

                            exchange.getResponseHeaders().set("Content-Type", "applications/json");
                            sendResponseHeaders(200, response, exchange);
                            sendResponseBody(response, exchange);
                        } else {
                            String response = "Такого параметра не существует!";
                            sendResponseHeaders(400, response, exchange);
                            sendResponseBody(response, exchange);
                        }
                    }
                    break;
                }
                default: {
                    String response = "Такой метод не поддерживается!";
                    sendResponseHeaders(400, response, exchange);
                    sendResponseBody(response, exchange);
                }
            }

        }
    }

    private static class SubtaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            switch (method) {
                case "GET": {
                    if (query == null) {
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        String response = gson.toJson(subtasks);

                        exchange.getResponseHeaders().set("Content-Type", "applications/json");
                        sendResponseHeaders(200, response, exchange);
                        sendResponseBody(response, exchange);
                    } else {
                        Map<String, String> params = parseQuery(query);
                        if (params.containsKey("id")) {
                            String response;
                            int id = Integer.parseInt(params.get("id"));
                            Subtask subtask = taskManager.getSubtask(id);

                            if (subtask == null) {
                                response = "Задачи с таким id не существует";
                                sendResponseHeaders(404, response, exchange);
                                sendResponseBody(response, exchange);
                            } else {
                                response = gson.toJson(subtask);
                                exchange.getResponseHeaders().set("Content-Type", "applications/json");
                                sendResponseHeaders(200, response, exchange);
                                sendResponseBody(response, exchange);
                            }
                        } else {
                            String response = "Такого параметра не существует!";
                            sendResponseHeaders(400, response, exchange);
                            sendResponseBody(response, exchange);
                        }
                    }
                    break;
                }
                case "POST": {
                    String response;
                    try (InputStream is = exchange.getRequestBody()) {
                        String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        taskManager.addSubtask(subtask);

                        boolean subtaskAlreadyExist = taskManager.getSubtasks().stream()
                                .anyMatch(existingSubtask -> existingSubtask.getID() == subtask.getID());
                        response = subtaskAlreadyExist ? "Подзадача обновлена" : "Подзадача добавлена";
                    } catch (Exception e) {
                        response = "Ошибка при обработке запроса: " + e.getMessage();
                        sendResponseHeaders(400, response, exchange);
                        sendResponseBody(response, exchange);
                    }
                    sendResponseHeaders(201, response, exchange);
                    sendResponseBody(response, exchange);
                    break;
                }
                case "DELETE": {
                    if (query == null) {
                        taskManager.clearSubtasks();
                        String response = "Подзадачи очищены";
                        sendResponseHeaders(200, response, exchange);
                        sendResponseBody(response, exchange);
                    } else {
                        Map<String, String> params = parseQuery(query);
                        if (params.containsKey("id")) {
                            int id = Integer.parseInt(params.get("id"));
                            Subtask subtask = taskManager.removeSubtask(id);
                            String response = gson.toJson(subtask);

                            exchange.getResponseHeaders().set("Content-Type", "applications/json");
                            sendResponseHeaders(200, response, exchange);
                            sendResponseBody(response, exchange);
                        } else {
                            String response = "Такого параметра не существует!";
                            sendResponseHeaders(400, response, exchange);
                            sendResponseBody(response, exchange);
                        }
                    }
                    break;
                }
            }
        }
    }

    private static class EpicHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            switch (method) {
                case "GET": {
                    if (query == null) {
                        List<Epic> epics = taskManager.getEpics();
                        String response = gson.toJson(epics);

                        exchange.getResponseHeaders().set("Content-Type", "applications/json");
                        sendResponseHeaders(200, response, exchange);
                        sendResponseBody(response, exchange);
                    } else {
                        Map<String, String> params = parseQuery(query);
                        if (params.containsKey("id")) {
                            int id = Integer.parseInt(params.get("id"));
                            Epic epic = taskManager.getEpic(id);
                            String response = gson.toJson(epic);

                            exchange.getResponseHeaders().set("Content-Type", "applications/json");
                            sendResponseHeaders(200, response, exchange);
                            sendResponseBody(response, exchange);
                        } else {
                            String response = "Такого параметра не существует!";
                            sendResponseHeaders(400, response, exchange);
                            sendResponseBody(response, exchange);
                        }
                    }
                    break;
                }
                case "POST": {
                    String response;
                    try (InputStream is = exchange.getRequestBody()) {
                        String body = new String(is.readAllBytes(), DEFAULT_CHARSET);
                        Epic epic = gson.fromJson(body, Epic.class);
                        List<Subtask> subtasks = epic.getSubtasks();
                        for (Subtask subtask : subtasks) {
                            taskManager.addSubtask(subtask);
                        }
                        taskManager.addEpic(epic);


                        boolean epicAlreadyExist = taskManager.getEpics().stream()
                                .anyMatch(existingEpic -> existingEpic.getID() == epic.getID());
                        response = epicAlreadyExist ? "Эпик обновлен" : "Эпик добавлен";
                    } catch (Exception e) {
                        response = "Ошибка при обработке запроса: " + e.getMessage();
                        sendResponseHeaders(400, response, exchange);
                        sendResponseBody(response, exchange);
                    }
                    sendResponseHeaders(201, response, exchange);
                    sendResponseBody(response, exchange);
                    break;
                }
                case "DELETE": {
                    if (query == null) {
                        taskManager.clearEpics();
                        String response = "Эпики очищены";
                        sendResponseHeaders(200, response, exchange);
                        sendResponseBody(response, exchange);
                    } else {
                        Map<String, String> params = parseQuery(query);
                        if (params.containsKey("id")) {
                            int id = Integer.parseInt(params.get("id"));
                            Epic epic = taskManager.removeEpic(id);
                            String response = gson.toJson(epic);

                            exchange.getResponseHeaders().set("Content-Type", "applications/json");
                            sendResponseHeaders(200, response, exchange);
                            sendResponseBody(response, exchange);
                        } else {
                            String response = "Такого параметра не существует!";
                            sendResponseHeaders(400, response, exchange);
                            sendResponseBody(response, exchange);
                        }
                    }
                    break;
                }
            }
        }
    }

    private static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            if (exchange.getRequestMethod().equals("GET")) {
                List<Task> history = taskManager.getHistory();
                response = history.isEmpty() ? "История пустая" : gson.toJson(history);

                sendResponseHeaders(200, response, exchange);
                sendResponseBody(response, exchange);
            } else {
                response = "Такой метод не поддерживается!";
                sendResponseHeaders(400, response, exchange);
                sendResponseBody(response, exchange);
            }
        }
    }

    private static class PrioritizedHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equals("GET")) {
                Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String jsonTasks = gson.toJson(prioritizedTasks);

                sendResponseHeaders(200, jsonTasks, exchange);
                sendResponseBody(jsonTasks, exchange);
            } else {
                exchange.sendResponseHeaders(400, 0);
                sendResponseBody("Такой метод не поддерживается!", exchange);
            }
        }
    }

    private static void sendResponseHeaders(int rCode, String response, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(rCode, response.getBytes(DEFAULT_CHARSET).length);
    }

    private static void sendResponseBody(String response, HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static void sendResponse(int statusCode, String responseBody, String contentType, HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        sendResponseHeaders(statusCode, responseBody, exchange);
        sendResponseBody(responseBody, exchange);
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

}
/*
{
  "name": "Task 1",
  "description": "Task Desc 1",
  "status": "NEW",
  "id": 1,
  "startTime": "12:00:00 01.01.2026",
  "duration": "PT1H30M"
}

{
  "epicID": 2,
  "name": "Sub 1",
  "description": "Sub Desc 1",
  "status": "NEW",
  "id": 3,
  "startTime": "09:00:00 03.03.2025",
  "duration": "PT30M"
}

{
  "epicID": 2,
  "name": "Sub 2",
  "description": "Sub Desc 2",
  "status": "NEW",
  "id": 4,
  "startTime": "10:00:00 03.03.2025",
  "duration": "PT1H"
}

{
  "subtasks": [
    {
      "epicID": 2,
      "name": "Sub 1",
      "description": "Sub Desc 1",
      "status": "NEW",
      "id": 3,
      "startTime": "09:00:00 03.03.2025",
      "duration": "PT30M"
    },
    {
      "epicID": 2,
      "name": "Sub 2",
      "description": "Sub Desc 2",
      "status": "NEW",
      "id": 4,
      "startTime": "10:00:00 03.03.2025",
      "duration": "PT1H"
    }
  ],
  "name": "Epic 1",
  "description": "Epic Desc 1",
  "status": "NEW",
  "id": 2,
  "startTime": "09:00:00 03.03.2025",
  "duration": "PT2H"
}*/