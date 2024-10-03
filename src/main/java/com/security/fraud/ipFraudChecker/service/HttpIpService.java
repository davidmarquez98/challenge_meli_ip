package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.http.HttpIpClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class HttpIpService {

    private final HttpIpClient httpClient = new HttpIpClient();

    public Mono<JSONObject> callApiCountryByIp(String ip){
        String url = "http://ip-api.com/json/" + ip;
        return httpClient.fetchApi(url);
    }

    public Mono<JSONObject> callApiCountryInfoByName(String country){
        String url = "https://restcountries.com/v3.1/name/" + country;
        return httpClient.fetchApi(url);
    }

    public Mono<JSONObject> callApiConversionCurrency(String currency){
        String url = "https://api.fxratesapi.com/latest?currencies=usd&base=" + currency;
        return httpClient.fetchApi(url);
    }
}
