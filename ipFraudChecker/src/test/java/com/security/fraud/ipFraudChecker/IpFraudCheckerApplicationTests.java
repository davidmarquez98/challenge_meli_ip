package com.security.fraud.ipFraudChecker;

import com.security.fraud.ipFraudChecker.commands.IpCommands;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@SpringBootTest
public class IpFraudCheckerApplicationTests {

	@Autowired
	private IpCommands ipCommands; // Asegúrate de que la clase que contiene 'traceip' se llame correctamente

	private static final int TOTAL_REQUESTS = 1_000_000; // Total de peticiones

	@Test
	@Timeout(10) // Establece un tiempo límite de 1 segundo para la prueba
	void loadTestTraceIp() {
		long startTime = System.currentTimeMillis();

		// Utiliza Flux para crear y enviar las peticiones
		Flux.range(0, TOTAL_REQUESTS)
				.flatMap(i -> Flux.just("192.0.2." + (i % 256))
						.publishOn(Schedulers.boundedElastic()) // Usa un scheduler que soporte un número variable de hilos
						.doOnNext(ipCommands::testTraceip))
				.blockLast(Duration.ofSeconds(1)); // Bloquea hasta que todas las peticiones se completen o se alcance el tiempo de espera

		long endTime = System.currentTimeMillis();
		System.out.println("Tiempo total: " + (endTime - startTime) + " ms");
	}
}
