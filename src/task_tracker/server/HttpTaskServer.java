package task_tracker.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import task_tracker.managers.Managers;
import task_tracker.managers.TaskManager;
import task_tracker.server.handlers.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpTaskServer {
    private static final int PORT = 8082;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final File file = new File("http-task-server-status.csv");
    private static final TaskManager taskManager = Managers.getFileBacked(file);
    private final HttpServer server;

    public HttpTaskServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера");
            throw new RuntimeException(e);
        }
    }

    public void start() {
        createEndpoints();
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    private void createEndpoints() {
        server.createContext("/tasks/task", new TaskHandler(taskManager));
        server.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
        server.createContext("/tasks/epic", new EpicHandler(taskManager));
        server.createContext("/tasks/history", new HistoryHandler(taskManager));
        server.createContext("/tasks", new PrioritizedHandler(taskManager));
    }

    public static void sendResponse(int statusCode, String responseBody, String contentType, HttpExchange exchange)
            throws IOException {
        byte[] bytes = responseBody.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().set("Content-Type", contentType);
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

    public static void sendPlainText(int statusCode, String message, HttpExchange exchange) throws IOException {
        sendResponse(statusCode, message, "text/plain; charset=UTF-8", exchange);
    }

    public static void sendJson(int statusCode, String jsonBody, HttpExchange exchange) throws IOException {
        sendResponse(statusCode, jsonBody, "application/json; charset=UTF-8", exchange);
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

}