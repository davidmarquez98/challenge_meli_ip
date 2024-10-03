package com.ipFraud.ipKafka.service;

import com.ipFraud.ipKafka.response.IpInfoResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, IpInfoResponse> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, IpInfoResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Void> sendMessage(String topic, IpInfoResponse message) {
        return Mono.fromRunnable(() -> kafkaTemplate.send(topic, message));
    }
}
