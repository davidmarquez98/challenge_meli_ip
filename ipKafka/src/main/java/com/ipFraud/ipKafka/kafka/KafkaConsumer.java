package com.ipFraud.ipKafka.kafka;

import com.ipFraud.ipKafka.mapper.IpResponseMapperImpl;
import com.ipFraud.ipKafka.mapper.IpResponseMapper;
import com.ipFraud.ipKafka.response.IpInfoResponse;
import com.ipFraud.ipKafka.service.HttpIpService;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConsumer {

    HttpIpService httpIpService = new HttpIpService();
    KafkaProducer kafkaProducer = new KafkaProducer();
    IpResponseMapper ipResponseMapper = new IpResponseMapperImpl();

    @KafkaListener(topics = "request-topic", groupId = "ip-group")
    private void listen(String ip){

        IpInfoResponse ipInfoEntity = new IpInfoResponse();
        ipInfoEntity.setIpAddress(ip);

        httpIpService.callApiCountryByIp(ip)
                .doOnSuccess(savedEntity -> System.out.println("callApiCountryByIp: " + savedEntity))
                .flatMap(countryApiResponse -> {

                    ipResponseMapper.fromJsonToResponse(countryApiResponse, ipInfoEntity);

                    return httpIpService.callApiCountryInfoByName(ipInfoEntity.getCountry())
                            .doOnSuccess(savedEntity -> System.out.println("callApiCountryInfoByName: " + savedEntity));
                })
                .flatMap(countryInfoApiResponse -> {

                    ipResponseMapper.fromJsonToResponse(countryInfoApiResponse, ipInfoEntity);

                    return httpIpService.callApiConversionCurrency(ipInfoEntity.getCurrency())
                            .doOnSuccess(savedEntity -> System.out.println("callApiConversionCurrency: " + savedEntity));
                })
                .flatMap(currencyApiResponse -> {

                    ipResponseMapper.fromJsonToResponse(currencyApiResponse, ipInfoEntity);

                    // ENVIAR KAFKA
                    return null;
                })
                .doOnSuccess(savedEntity -> System.out.println("Guardado: " + savedEntity))
                .doOnError(error -> System.err.println("Error al guardar la entidad: " + error.getMessage()));
    }

}
