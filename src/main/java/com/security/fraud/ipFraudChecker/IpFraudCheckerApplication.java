package com.security.fraud.ipFraudChecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IpFraudCheckerApplication {

	public static void main(String[] args) {

		//SpringApplication.run(IpFraudCheckerApplication.class, args);
		ConnectionFactory connectionFactory = ConnectionFactories.get("r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

		R2dbcEntityTemplate template = new R2dbcEntityTemplate(connectionFactory);

		template.getDatabaseClient().sql("CREATE TABLE person" +
						"(id VARCHAR(255) PRIMARY KEY," +
						"name VARCHAR(255)," +
						"age INT)")
				.fetch()
				.rowsUpdated()
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();

		template.insert(Person.class)
				.using(new Person("joe", "Joe", 34))
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();

		template.select(Person.class)
				.first()
				.doOnNext(it -> log.info(it))
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();

	}

}
