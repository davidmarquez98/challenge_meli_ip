package com.security.fraud.ipFraudChecker.commands;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.service.EstadisticasService;
import com.security.fraud.ipFraudChecker.service.IpService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @ShellMethod(key = "traceip")
    public void traceip(@ShellOption String ip) {
        ipService.getIpInfo(ip)
                .flatMap(this::formatIpInfo)
                .doOnNext(System.out::println)
                .subscribe();
    }

    private Mono<String> formatIpInfo(IpInfoEntity ipInfo) {
        // Obtener las distancias
        Mono<Double> minDistance = ipService.getMinDistance();
        Mono<Double> maxDistance = ipService.getMaxDistance();
        Mono<Double> avgDistance = estadisticasService.getPromedioDistancias(ipInfo.getEstimatedDistance());

        // Combinar la información de la IP con las distancias
        return Mono.zip(minDistance, maxDistance, avgDistance)
                .map(tuple -> {
                    // Formatear la fecha actual
                    String currentDate = LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    // Usar StringBuilder para crear el mensaje
                    StringBuilder message = new StringBuilder();
                    message.append(String.format("IP: %s, fecha actual: %s\n", ipInfo.getIpAddress(), currentDate))
                            .append(String.format("País: %s\n", ipInfo.getCountry()))
                            .append(String.format("ISO Code: %s\n", ipInfo.getIsoCode()))
                            .append(String.format("Idiomas: %s\n", ipInfo.getLanguages()))
                            .append(String.format("Moneda: %s\n", ipInfo.getCurrency()))
                            .append(String.format("Hora: %s\n", ipInfo.getCurrentLocalTime()))
                            .append(String.format("Distancia estimada: %s\n", ipInfo.getMessageEstimatedDistance()))
                            .append(String.format("Distancia más cercana a Buenos Aires: %.2f km\n", tuple.getT1()))
                            .append(String.format("Distancia más lejana a Buenos Aires: %.2f km\n", tuple.getT2()))
                            .append(String.format("Distancia promedio de todas las ejecuciones: %.2f km", tuple.getT3()));

                    return message.toString();
                });
    }
}
