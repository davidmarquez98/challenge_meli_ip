package com.ipFraud.ipKafka.kafka;

import com.ipFraud.ipKafka.mapper.IpResponseMapperImpl;
import com.ipFraud.ipKafka.mapper.IpResponseMapper;
import com.ipFraud.ipKafka.response.IpInfoResponse;
import com.ipFraud.ipKafka.service.HttpIpService;
import org.springframework.kafka.annotation.KafkaListener;

public class KafkaConsumer {

    HttpIpService httpIpService = new HttpIpService();
    KafkaProducer kafkaProducer;
    IpResponseMapper ipResponseMapper = new IpResponseMapperImpl();

    @KafkaListener(topics = "request-topic", groupId = "ip-group")
    private void listen(String ip){

        IpInfoResponse ipInfoResponse = new IpInfoResponse();
        ipInfoResponse.setIpAddress(ip);

        httpIpService.callApiCountryByIp(ip)
                .doOnSuccess(savedEntity -> System.out.println("callApiCountryByIp: " + savedEntity))
                .flatMap(countryApiResponse -> {

                    ipResponseMapper.fromJsonToResponse(countryApiResponse, ipInfoResponse);
                    return httpIpService.callApiCountryInfoByName(ipInfoResponse.getCountry())
                            .doOnSuccess(savedEntity -> System.out.println("callApiCountryInfoByName: " + savedEntity));
                })
                .flatMap(countryInfoApiResponse -> {

                    ipResponseMapper.fromJsonToResponse(countryInfoApiResponse, ipInfoResponse);
                    return httpIpService.callApiConversionCurrency(ipInfoResponse.getCurrency())
                            .doOnSuccess(savedEntity -> System.out.println("callApiConversionCurrency: " + savedEntity));
                })
                .doOnNext(currencyApiResponse -> {

                    ipResponseMapper.fromJsonToResponse(currencyApiResponse, ipInfoResponse);
                    kafkaProducer.sendIpRequest(ipInfoResponse);
                })
                .doOnSuccess(savedEntity -> System.out.println("Guardado: " + savedEntity))
                .doOnError(error -> System.err.println("Error al guardar la entidad: " + error.getMessage()));
    }

}
