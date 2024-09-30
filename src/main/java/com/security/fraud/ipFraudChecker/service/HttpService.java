package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.http.HttpClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class HttpService {

    private final HttpClient httpClient = new HttpClient();

    public Mono<JSONObject> callApiCountryByIp(String ip) throws IOException {

        String url = "http://ip-api.com/json/" + ip;

        return httpClient.fetchApi(url);
    }

    public Mono<JSONObject> callApiCountryInfoByName(String country) throws IOException {

        String url = "https://restcountries.com/v3.1/name/" + country;

        return httpClient.fetchApi(url);
    }

    public Mono<JSONObject> callApiConversionCurrency(String currency) throws IOException {

        String url = "https://api.fxratesapi.com/latest?currencies=usd&base=" + currency;

        return httpClient.fetchApi(url);
    }
}
