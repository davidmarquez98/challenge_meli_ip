package com.security.fraud.ipFraudChecker;

import com.security.fraud.ipFraudChecker.commands.IpCommands;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class IpFraudCheckerApplicationTests {

	@Autowired
	private IpCommands ipCommands;

	private static final int TOTAL_REQUESTS = 1_000_000;

	@Test
	public void testTraceipWithHighLoad() throws InterruptedException {
		int numRequests = TOTAL_REQUESTS;
		ExecutorService executor = Executors.newFixedThreadPool(100);

		for (int i = 0; i < numRequests; i++) {
			String ip = "192.168.1." + (i % 255);

			executor.submit(() -> ipCommands.traceip(ip));
		}

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);
	}
}
