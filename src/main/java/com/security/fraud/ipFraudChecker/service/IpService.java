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
                        System.out.println("No se encontró la entidad, construyendo desde la API");
                        return buildIpInfoFromApi(ip);
                }));
    }

    private Mono<IpInfoEntity> buildIpInfoFromApi(String ip){
        IpInfoEntity ipInfoEntity = new IpInfoEntity();
        ipInfoEntity.setIpAddress(ip);

        // Llamada a la primera API para obtener información del país por IP
        return httpService.callApiCountryByIp(ip)
                .doOnSuccess(savedEntity -> System.out.println("callApiCountryByIp: " + savedEntity))
                .flatMap(countryApiResponse -> {
                    // Mapear la respuesta de la primera API a la entidad
                    ipInfoMapper.fromJsonToEntity(countryApiResponse, ipInfoEntity);

                    // Llamada a la segunda API usando la información del país
                    return httpService.callApiCountryInfoByName(ipInfoEntity.getCountry())
                            .doOnSuccess(savedEntity -> System.out.println("callApiCountryInfoByName: " + savedEntity));
                })
                .flatMap(countryInfoApiResponse -> {
                    // Mapear la respuesta de la segunda API
                    ipInfoMapper.fromJsonToEntity(countryInfoApiResponse, ipInfoEntity);

                    // Llamada a la tercera API usando la información de la moneda obtenida
                    return httpService.callApiConversionCurrency(ipInfoEntity.getCurrency())
                            .doOnSuccess(savedEntity -> System.out.println("callApiConversionCurrency: " + savedEntity));
                })
                .flatMap(currencyApiResponse -> {
                    // Mapear la respuesta de la tercera API y completar la entidad
                    ipInfoMapper.fromJsonToEntity(currencyApiResponse, ipInfoEntity);
                    ipInfoEntity.setInvocations(1);

                    // Guardar la entidad en la base de datos
                    return ipRepository.save(ipInfoEntity);
                })
                .doOnSuccess(savedEntity -> System.out.println("Guardado: " + savedEntity))
                .doOnError(error -> System.err.println("Error al guardar la entidad: " + error.getMessage()));
    }

//    private Mono<IpInfoEntity> buildIpInfoFromApi(String ip) throws IOException {
//
//        IpInfoEntity ipInfoEntity = new IpInfoEntity();
//
//        return httpService
//                .callApiCountryByIp(ip)
//
//                .flatMap(countryApiResponse -> {
//
//                    ipInfoMapper.fromJsonToEntity(countryApiResponse, ipInfoEntity);
//
//                    ipInfoEntity.setIpAddress(ip);
//
//                    try {
//                        return httpService.callApiCountryInfoByName(ipInfoEntity.getCountry());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//
//                .flatMap(secondApiResponse -> {
//
//                    ipInfoMapper.fromJsonToEntity(secondApiResponse, ipInfoEntity);
//
//                    try {
//                        return httpService.callApiConversionCurrency(ipInfoEntity.getCurrency());
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
//
//                .flatMap(thirdApiResponse -> {
//
//                    ipInfoMapper.fromJsonToEntity(thirdApiResponse, ipInfoEntity);
//                    ipInfoEntity.setInvocations(1);
//
//                    ipRepository.save(ipInfoEntity)
//                            .doOnSuccess(savedEntity -> System.out.println("Guardado: " + savedEntity))
//                            .doOnError(error -> System.err.println("Error al guardar la entidad: " + error.getMessage()))
//                            .subscribe();
//
//                    return Mono.just(ipInfoEntity);
//                });
//    }
}

