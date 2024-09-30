package com.security.fraud.ipFraudChecker.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class HttpClient{

    private final WebClient webClient;

    public HttpClient() {
        this.webClient = WebClient.create();
    }

    public Mono<JSONObject> fetchApi(String url) {
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> {
                            // Registrar el código de estado y cuerpo del error
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        System.err.println("HTTP Status: " + clientResponse.statusCode());
                                        System.err.println("Error body: " + errorBody);
                                        return Mono.error(new RuntimeException("Error fetching data: " + clientResponse.statusCode()));
                                    });
                        })

                .bodyToMono(String.class)
                .map(responseBody -> {

                    JSONObject jsonObjectResponse = null;

                    try {

                        if (responseBody.trim().startsWith("[")) {
                            JSONArray jsonArray = new JSONArray(responseBody);

                            if (!jsonArray.isEmpty()) {
                                jsonObjectResponse = jsonArray.getJSONObject(0);
                            }
                        } else {
                            jsonObjectResponse = new JSONObject(responseBody);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException("Invalid JSON response", e);
                    }

                    return jsonObjectResponse;
                })
                .doOnSuccess(result -> {
                    // Registrar la respuesta
                    System.out.println("Response: " + result);
                })
                .doOnError(error -> {
                    // Registrar el error
                    System.err.println("Error: " + error.getMessage());
                });
    }

}

