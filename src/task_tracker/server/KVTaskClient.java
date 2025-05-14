package task_tracker.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final URI url;
    private final String apiKey;

    public KVTaskClient(URI url) {
        this.url = url;
        this.apiKey = register();
    }

    private String register() {
        URI registerUrl = getUrl("/register");
        return getResponseBody(registerUrl);
    }

    public void put(String key, String json) {
        String savePath = String.format("/save/%s?API_KEY=%s", key, apiKey);
        URI saveUrl = getUrl(savePath);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(saveUrl)
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Статус код не соответствует ожидаемому");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка сохранения запроса по ключу " + key);
        }
    }

    public String load(String key) {
        String savePath = String.format("/load/%s?API_KEY=%s", key, apiKey);
        URI loadUrl = getUrl(savePath);

        return getResponseBody(loadUrl);
    }

    private URI getUrl(String savePath) {
        URI loadUrl;
        try {
            loadUrl = new URI(url.toString() + savePath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return loadUrl;
    }

    private String getResponseBody(URI loadUrl) {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(loadUrl).build();
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
