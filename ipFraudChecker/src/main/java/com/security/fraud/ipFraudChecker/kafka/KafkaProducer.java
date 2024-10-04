package com.security.fraud.ipFraudChecker.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    // Constructor que recibe el KafkaTemplate
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendIpRequest(String ipRequest) {
        System.out.println("sendIpRequest - entrando..");
        kafkaTemplate.send("request-topic", ipRequest);
    }
}
