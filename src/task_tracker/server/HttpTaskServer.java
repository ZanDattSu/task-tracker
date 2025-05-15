package task_tracker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import task_tracker.managers.Managers;
import task_tracker.managers.TaskManager;
import task_tracker.server.handlers.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final HttpServer server;

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        createEndpoints();
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
    }

    private void createEndpoints() {
        server.createContext("/tasks/task", new TaskHandler(taskManager));
        server.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        server.createContext("/tasks/epic", new EpicHandler(taskManager));
        server.createContext("/tasks/subtask/epic", new SubtasksOfEpicHandler(taskManager));
        server.createContext("/tasks/history", new HistoryHandler(taskManager));
        server.createContext("/tasks", new PrioritizedHandler(taskManager));
    }

    public static void sendText(int statusCode, String text, HttpExchange exchange) throws IOException {
        byte[] bytes = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isBlank()) {
            return params;
        }

        for (String pair : query.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    public static int getIdFromQuery(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();

        if (query == null || query.isBlank()) {
            sendText(400, "Отсутствуют параметры запроса", exchange);
            throw new IllegalArgumentException("Отсутствуют параметры запроса");
        }

        Map<String, String> params = HttpTaskServer.parseQuery(query);
        if (!params.containsKey("id")) {
            sendText(400, "Ожидался параметр 'id'", exchange);
            throw new IllegalArgumentException("Ожидался параметр 'id'");
        }
        return Integer.parseInt(params.get("id"));
    }
}