package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapperImpl;
import com.security.fraud.ipFraudChecker.mapper.IpInfoMapper;
import com.security.fraud.ipFraudChecker.repository.IpRepository;
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

    private Mono<IpInfoEntity> getIpInfoExtern(String ip){
        return ipRepository.findByIpAddress(ip)

                // Pais con dicho ip existe
                .flatMap(ipInfoEntityFound -> {

                    System.out.println("Entidad encontrada: " + ipInfoEntityFound);
                    int invocations = ipInfoEntityFound.getInvocations();
                    invocations = invocations + 1;
                    ipInfoEntityFound.setInvocations(invocations);

                    ipRepository.save(ipInfoEntityFound).subscribe();

                    return Mono.just(ipInfoEntityFound);
                })

                // Pais con dicho ip no existe
                .switchIfEmpty(Mono.defer(() -> {
                    try {
                        System.out.println("No se encontró la entidad, construyendo desde la API");
                        return buildIpInfoFromApi(ip);
                    } catch (IOException e) {
                        return Mono.error(new RuntimeException("Error al construir desde la API", e));
                    }
                }));
    }

    private Mono<IpInfoEntity> buildIpInfoFromApi(String ip) throws IOException {

        IpInfoEntity ipInfoEntity = new IpInfoEntity();

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

                    ipRepository.save(ipInfoEntity)
                            .doOnSuccess(savedEntity -> System.out.println("Guardado: " + savedEntity))
                            .doOnError(error -> System.err.println("Error al guardar la entidad: " + error.getMessage()))
                            .subscribe();

                    return Mono.just(ipInfoEntity);
                });
    }
}

