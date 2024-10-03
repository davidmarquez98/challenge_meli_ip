package com.security.fraud.ipFraudChecker.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IpInfoDTO{

    private String ipAddress;

    private LocalDateTime timestamp;

    private String country;

    private String isoCode;

    private String languages;

    private String currency;

    private String currencyConversion;

    private String currentTime;

    private String estimatedDistance;

}