package com.security.fraud.ipFraudChecker.kafka;

import com.security.fraud.ipFraudChecker.service.IpService;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConsumer {

    IpService ipService = new IpService();

    // Este método será llamado cuando se reciba un mensaje en el topic "response-topic"
    @KafkaListener(topics = "response-topic", groupId = "ip-response-group")
    public void listen(String message) {
        JSONObject jsonResponse = new JSONObject(message);
        ipService.receiveResponseFromKafka(jsonResponse);
    }
}
