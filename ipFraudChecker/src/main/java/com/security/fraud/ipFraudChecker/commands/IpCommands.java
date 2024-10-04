package com.security.fraud.ipFraudChecker.commands;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.service.EstadisticasService;
import com.security.fraud.ipFraudChecker.service.IpService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@ShellComponent
public class IpCommands {

    private final EstadisticasService estadisticasService = new EstadisticasService();

    @Autowired
    private IpService ipService;

    private final String reponseMessage = "IP: %s, fecha actual: %s\nPaís: %s \nISO Code: %s\nIdiomas: %s\nMoneda: %s\nHora: %s \nDistancia estimada: %s\nDistancia más cercana a Buenos Aires: %.2f km" +
                                          "\nDistancia más lejana a Buenos Aires: %.2f km\nDistancia promedio de todas las ejecuciones que se hayan hecho del servicio: %.2f km";


    // ENVIO, SI EXISTE, MUESTRA
    @ShellMethod(key = "traceip")
    public void traceip(@ShellOption String ip){
        ipService.getIpInfo(ip)
                 .flatMap(this::formatIpInfo)
                 .doOnNext(System.out::println)
                 .switchIfEmpty(Mono.empty()).subscribe();
    }

    // RECIBE, SI NO EXISTE
    public void showIpInfo(IpInfoEntity ipInfoEntity){
        formatIpInfo(ipInfoEntity).doOnNext(System.out::println).subscribe();
    }


    private Mono<String> formatIpInfo(IpInfoEntity ipInfo) {

        Mono monoResponse = Mono.empty();

        if(ipInfo != null){
            // Obtener las distancias
            Mono<Double> minDistance = ipService.getMinDistance();
            Mono<Double> maxDistance = ipService.getMaxDistance();
            Mono<Double> avgDistance = estadisticasService.getPromedioDistancias(ipInfo.getEstimatedDistance());

            // Combinar la información de la IP con las distancias
            Mono.zip(minDistance, maxDistance, avgDistance)
                    .doOnNext(tuple -> {
                        Double minDist = tuple.getT1();       // Distancia mínima
                        Double maxDist = tuple.getT2();       // Distancia máxima
                        Double avgDist = tuple.getT3();       // Distancia máxima

                        // Formatear la fecha actual
                        String currentDate = LocalDateTime.now().toString();
                        monoResponse.map(ignored -> String.format(this.reponseMessage,
                                                                        ipInfo.getIpAddress(),
                                                                        currentDate,
                                                                        ipInfo.getCountry(),
                                                                        ipInfo.getIsoCode(),
                                                                        ipInfo.getLanguages(),
                                                                        ipInfo.getCurrency(),
                                                                        ipInfo.getCurrentLocalTime(),
                                                                        ipInfo.getMessageEstimatedDistance(),
                                                                        minDist,
                                                                        maxDist,
                                                                        avgDist));
                    });
        }

        return monoResponse;
    }
}
