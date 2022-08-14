package clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static managers.util.Constants.UTF_8;

public class KVTaskClient {
    private final HttpClient client;
    private final String apiToken;
    private final String serverUrl;

    public KVTaskClient(String url) {
        this.serverUrl = url;
        this.client = HttpClient.newHttpClient();
        this.apiToken = getApiToken(client, url);
    }

    public void put(String key, String json) {
        URI putUri = URI.create(serverUrl + "save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(putUri)
                .POST(HttpRequest.BodyPublishers.ofString(json, UTF_8))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        } catch (IOException | InterruptedException e) {
            System.out.println("Error during /save request");
        }
    }

    public String load(String key) {
        URI loadUri = URI.create(serverUrl + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(loadUri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error during /load request");
        }
        return null;
    }

    private String getApiToken(HttpClient client, String url) {
        URI apiUrl = URI.create(url + "register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiUrl)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error during /register request");
        }
        return null;
    }
}
