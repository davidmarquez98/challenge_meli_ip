package com.ipFraud.ipKafka.kafka;

import com.ipFraud.ipKafka.response.IpInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, IpInfoResponse> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, IpInfoResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendIpRequest(IpInfoResponse ipRequest) {
        kafkaTemplate.send("request-topic", ipRequest);
    }
}
