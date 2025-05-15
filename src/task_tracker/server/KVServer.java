package task_tracker.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import task_tracker.managers.ManagerSaveException;
import task_tracker.utils.GsonProvider;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVServer {
    public static final int PORT = 8078;
    private static final File STORAGE_FILE = new File("data-storage.json");
    private static final Gson gson = GsonProvider.getGson();
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data;

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
        this.data = loadFromFile();
    }

    public static URI getUrl() {
        try {
            return new URI("http://localhost:" + PORT);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void load(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Запрос не авторизован, нужен параметр в query API_KEY со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String response = data.get(key);
                if (response == null) {
                    System.out.println("По Key=" + key + " значений не найдено");
                    h.sendResponseHeaders(404, 0);
                    return;
                }
                sendText(h, response);
                System.out.println("Значение для ключа " + key + " успешно возвращено!");

            } else {
                System.out.println("/load ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void save(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос не авторизован, нужен параметр в query API_KEY со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                saveToFile();
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void register(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    public void start() {
        server.start();
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_KEY: " + apiToken);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_KEY=" + apiToken) || rawQuery.contains("API_KEY=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    private void saveToFile() {
        try (FileWriter writer = new FileWriter(STORAGE_FILE)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл: " + e.getMessage());
        }
    }

    private Map<String, String> loadFromFile() {
        if (!STORAGE_FILE.exists()) {
            return new HashMap<>();
        }
        try (Reader reader = new FileReader(STORAGE_FILE, UTF_8)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            System.out.println("Ошибка чтения из файла: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
