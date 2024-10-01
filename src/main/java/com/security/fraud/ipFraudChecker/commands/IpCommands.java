package com.security.fraud.ipFraudChecker.commands;

import com.security.fraud.ipFraudChecker.entity.EstadisticasEntity;
import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import com.security.fraud.ipFraudChecker.service.IpService;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.IOException;

@ShellComponent
public class IpCommands {

    private final IpService ipService;

    public IpCommands(IpService ipService) {
        this.ipService = ipService;
    }

    @ShellMethod(key = "hello-world")
    public String helloWorld(@ShellOption(defaultValue = "spring") String arg) {
        return "Hello world " + arg;
    }

    @ShellMethod(key = "traceip")
    public String traceip(@ShellOption String ip) throws IOException {
        //EstadisticasEntity estadisticasEntity = ipService.getEstadisticas(ipInfoEnitity);

        return ipService.getIpInfo(ip).map(ipInfoEnitityFromMono -> formatIpInfo(ipInfoEnitityFromMono)).block();
    }

    private String formatIpInfo(IpInfoEntity ipInfo) {

        System.out.println("---------------------------");

        // Formato de fecha actual
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String currentDate = LocalDateTime.now().format(dateFormatter);

        // Aquí formatearías la información para mostrarla de manera legible en la consola
        return String.format("IP: %s, fecha actual: %s\nPaís: %s \nISO Code: %s\nIdiomas: %s\nMoneda: %s\nHora: %s \nDistancia estimada: %s",
                ipInfo.getIpAddress(),
                currentDate,
                ipInfo.getCountry(),
                ipInfo.getIsoCode(),
                ipInfo.getLanguages(),
                ipInfo.getCurrency(),
                ipInfo.getCurrentLocalTime(),
                ipInfo.getEstimatedDistance());
    }
}
