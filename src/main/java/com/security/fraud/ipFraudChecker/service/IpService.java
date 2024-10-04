package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapper;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapperImpl;
import com.security.fraud.ipFraudChecker.repository.IpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IpService {

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

    public Mono<IpInfoEntity> getIpInfo(String ip) {
        return getIpInfoExtern(ip);
    }

    private Mono<IpInfoEntity> getIpInfoExtern(String ip) {
        return ipRepository.findByIpAddress(ip)
                .flatMap(this::updateInvocations)
                .switchIfEmpty(Mono.defer(() -> buildIpInfoFromApi(ip)));
    }

    private Mono<IpInfoEntity> updateInvocations(IpInfoEntity ipInfoEntityFound) {
        ipInfoEntityFound.setInvocations(ipInfoEntityFound.getInvocations() + 1);

        return ipRepository.save(ipInfoEntityFound)
                .then(Mono.just(ipInfoEntityFound));
    }

    private Mono<IpInfoEntity> buildIpInfoFromApi(String ip) {
        IpInfoEntity ipInfoEntity = new IpInfoEntity();
        ipInfoEntity.setIpAddress(ip);

        return httpService.callApiCountryByIp(ip)
                .flatMap(countryApiResponse -> {
                    ipInfoMapper.fromJsonToEntity(countryApiResponse, ipInfoEntity);
                    return httpService.callApiCountryInfoByName(ipInfoEntity.getCountry());
                })
                .flatMap(countryInfoApiResponse -> {
                    ipInfoMapper.fromJsonToEntity(countryInfoApiResponse, ipInfoEntity);
                    return httpService.callApiConversionCurrency(ipInfoEntity.getCurrency());
                })
                .flatMap(currencyApiResponse -> {
                    ipInfoMapper.fromJsonToEntity(currencyApiResponse, ipInfoEntity);
                    ipInfoEntity.setInvocations(1);
                    return ipRepository.save(ipInfoEntity);
                });
    }
}
