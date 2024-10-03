package com.ipFraud.ipKafka.response;

import lombok.Data;

@Data
public class IpInfoResponse {

    private String ipAddress;
    private String datetime;
    private String country;
    private String isoCode;
    private String languages;
    private String currency;
    private String currencyConversion;
    private String currentLocalTime;
    private String messageEstimatedDistance;
    private Double estimatedDistance;

}
