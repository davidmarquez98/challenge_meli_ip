package com.ipFraud.ipKafka.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

public class HttpIpClient {

    private final WebClient webClient;

    public HttpIpClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(50)  // Máximo número de conexiones
                .pendingAcquireTimeout(Duration.ofSeconds(60))  // Tiempo máximo de espera
                .maxIdleTime(Duration.ofSeconds(30))  // Tiempo máximo de inactividad
                .maxLifeTime(Duration.ofMinutes(5))  // Tiempo máximo de vida de una conexión
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(10));  // Tiempo máximo de espera para la respuesta

        // Crear WebClient con HttpClient configurado
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))  // Configuración del WebClient
                .build();
    }


    public Mono<JSONObject> fetchApi(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("HTTP Status: " + clientResponse.statusCode());
                                    System.err.println("Error body: " + errorBody);
                                    return Mono.error(new RuntimeException("Error fetching data: " + clientResponse.statusCode()));
                                }))
                .bodyToMono(String.class)
                .map(this::parseJsonResponse);
    }

    private JSONObject parseJsonResponse(String responseBody) {
        try {
            if (responseBody.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(responseBody);
                return jsonArray.length() > 0 ? jsonArray.getJSONObject(0) : null;
            } else {
                return new JSONObject(responseBody);
            }
        } catch (JSONException e) {
            throw new RuntimeException("Invalid JSON response", e);
        }
    }

}

