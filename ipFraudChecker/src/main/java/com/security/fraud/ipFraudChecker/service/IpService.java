package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.commands.IpCommands;
import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.kafka.KafkaProducer;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapperImpl;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapper;
import com.security.fraud.ipFraudChecker.repository.IpRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IpService{

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    KafkaProducer kafkaProducer;

    IpInfoMapper ipInfoMapper = new IpInfoMapperImpl();

    IpCommands ipCommands = new IpCommands();


    public Mono<Double> getMinDistance() {
        return ipRepository.findMinDistance();
    }

    public Mono<Double> getMaxDistance() {
        return ipRepository.findMaxDistance();
    }


    public Mono<IpInfoEntity> getIpInfo(String ip) {

        return ipRepository.findByIpAddress(ip)
                .doOnError(error -> System.err.println("Error occurred: " + error.getMessage()))
                .flatMap(ipInfoEntityFound -> {
                    System.out.println("IP encontrada: " + ipInfoEntityFound);
                    ipInfoEntityFound.setInvocations(ipInfoEntityFound.getInvocations() + 1);
                    return ipRepository.save(ipInfoEntityFound);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    System.out.println("Enviando IP a Kafka");
                    kafkaProducer.sendIpRequest(ip);
                    return Mono.empty();
                }));
    }


    public void receiveResponseFromKafka(JSONObject jsonResponse){
        IpInfoEntity ipInfoEntity = new IpInfoEntity();

        ipInfoMapper.fromJsonToEntity(jsonResponse, ipInfoEntity);
        ipRepository.save(ipInfoEntity);

        ipCommands.showIpInfo(ipInfoEntity);
    }


    IpInfoEntity ipInfoEntity = new IpInfoEntity();
}

