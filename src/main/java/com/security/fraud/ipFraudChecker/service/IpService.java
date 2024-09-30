package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapperImpl;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapper;
import com.security.fraud.ipFraudChecker.repository.IpRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class IpService{

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private HttpService httpService;

    private final IpInfoMapper ipInfoMapper = new IpInfoMapperImpl();


    public Mono<IpInfoEntity> getIpInfo(String ip) throws IOException {
        return getIpInfoExtern(ip);
    }

    private Mono<IpInfoEntity> getIpInfoExtern(String ip) throws IOException {
        return ipRepository.findByIpAddress(ip)
                .switchIfEmpty(buildIpInfoFromApi(ip)); // Llama a la API si no se encuentra la IP
    }

    private Mono<IpInfoEntity> buildIpInfoFromApi(String ip) throws IOException {

        IpInfoEntity ipInfoEntity = new IpInfoEntity();

        System.out.println("buildIpInfoFromApi - NO EXISTE");

        return httpService
                .callApiCountryByIp(ip)

                .flatMap(countryApiResponse -> {

                    ipInfoMapper.fromJsonToEntity(countryApiResponse, ipInfoEntity);

                    ipInfoEntity.setIpAddress(ip);

                    try {
                        return httpService.callApiCountryInfoByName(ipInfoEntity.getCountry());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })

                .flatMap(secondApiResponse -> {

                    ipInfoMapper.fromJsonToEntity(secondApiResponse, ipInfoEntity);

                    try {
                        return httpService.callApiConversionCurrency(ipInfoEntity.getCurrency());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })

                .flatMap(thirdApiResponse -> {

                    ipInfoMapper.fromJsonToEntity(thirdApiResponse, ipInfoEntity);
                    ipInfoEntity.setInvocations(1);

                    ipRepository.save(ipInfoEntity);

                    return Mono.just(ipInfoEntity);
                });
    }
}

