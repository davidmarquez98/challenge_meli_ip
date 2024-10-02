package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapperImpl;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapper;
import com.security.fraud.ipFraudChecker.repository.IpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IpService{

    @Autowired
    private IpRepository ipRepository;

    @Autowired
    private HttpIpService httpService;

    private final IpInfoMapper ipInfoMapper = new IpInfoMapperImpl();

    public Mono<Double> getMinDistance() {
        return ipRepository.findMinDistance();
    }

    public Mono<Double> getMaxDistance() {
        return ipRepository.findMaxDistance();
    }

    public Mono<Double> getAverageDistance() {
        return ipRepository.findAverageDistance();
    }


    public Mono<IpInfoEntity> getIpInfo(String ip){
        return getIpInfoExtern(ip);
    }

    private Mono<IpInfoEntity> getIpInfoExtern(String ip){
        return ipRepository.findByIpAddress(ip)

                // Pais con dicho ip existe
                .flatMap(ipInfoEntityFound -> {

                    int invocations = ipInfoEntityFound.getInvocations();
                    invocations = invocations + 1;
                    ipInfoEntityFound.setInvocations(invocations);

                    ipRepository.save(ipInfoEntityFound).subscribe();

                    return Mono.just(ipInfoEntityFound);
                })

                // Pais con dicho ip no existe
                .switchIfEmpty(Mono.defer(() -> buildIpInfoFromApi(ip)));
    }

    private Mono<IpInfoEntity> buildIpInfoFromApi(String ip){

        IpInfoEntity ipInfoEntity = new IpInfoEntity();
        ipInfoEntity.setIpAddress(ip);

        return httpService.callApiCountryByIp(ip)
                .doOnSuccess(savedEntity -> System.out.println("callApiCountryByIp: " + savedEntity))
                .flatMap(countryApiResponse -> {

                    ipInfoMapper.fromJsonToEntity(countryApiResponse, ipInfoEntity);

                    return httpService.callApiCountryInfoByName(ipInfoEntity.getCountry())
                            .doOnSuccess(savedEntity -> System.out.println("callApiCountryInfoByName: " + savedEntity));
                })
                .flatMap(countryInfoApiResponse -> {

                    ipInfoMapper.fromJsonToEntity(countryInfoApiResponse, ipInfoEntity);

                    return httpService.callApiConversionCurrency(ipInfoEntity.getCurrency())
                            .doOnSuccess(savedEntity -> System.out.println("callApiConversionCurrency: " + savedEntity));
                })
                .flatMap(currencyApiResponse -> {

                    ipInfoMapper.fromJsonToEntity(currencyApiResponse, ipInfoEntity);
                    ipInfoEntity.setInvocations(1);

                    return ipRepository.save(ipInfoEntity);
                })
                .doOnSuccess(savedEntity -> System.out.println("Guardado: " + savedEntity))
                .doOnError(error -> System.err.println("Error al guardar la entidad: " + error.getMessage()));
    }

}

