package com.security.fraud.ipFraudChecker.commands;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.service.EstadisticasService;
import com.security.fraud.ipFraudChecker.service.IpService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@ShellComponent
public class IpCommands {

    private final EstadisticasService estadisticasService;

    private final IpService ipService;

    public IpCommands(EstadisticasService estadisticasService, IpService ipService) {
        this.estadisticasService = estadisticasService;
        this.ipService = ipService;
    }

    @ShellMethod(key = "hello-world")
    public String helloWorld(@ShellOption(defaultValue = "spring") String arg) {
        return "Hello world " + arg;
    }

    @ShellMethod(key = "traceip")
    public void traceip(@ShellOption String ip){
        ipService.getIpInfo(ip)
                 .flatMap(ipInfoEntityFromMono -> formatIpInfo(ipInfoEntityFromMono))
                 .doOnNext(a -> System.out.println(a)).subscribe();
    }


    private Mono<String> formatIpInfo(IpInfoEntity ipInfo) {

        // Obtener las distancias
        Mono<Double> minDistance = ipService.getMinDistance();
        Mono<Double> maxDistance = ipService.getMaxDistance();
        Mono<Double> avgDistance = estadisticasService.getPromedioDistancias(ipInfo.getEstimatedDistance());

        // Combinar la información de la IP con las distancias
        return Mono.zip(minDistance, maxDistance, avgDistance)
                .map(tuple -> {
                    Double minDist = tuple.getT1();       // Distancia mínima
                    Double maxDist = tuple.getT2();       // Distancia máxima
                    Double avgDist = tuple.getT3();       // Distancia máxima

                    // Formatear la fecha actual
                    String currentDate = LocalDateTime.now().toString();

                    // Formatear el mensaje final
                    return String.format(
                            "IP: %s, fecha actual: %s\nPaís: %s \nISO Code: %s\nIdiomas: %s\nMoneda: %s\nHora: %s \nDistancia estimada: %s\n" +
                                    "Distancia más cercana a Buenos Aires: %.2f km\nDistancia más lejana a Buenos Aires: %.2f km\nDistancia promedio de todas las ejecuciones que se hayan hecho del servicio: %.2f km",
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
                            avgDist
                    );
                });

    }
}
