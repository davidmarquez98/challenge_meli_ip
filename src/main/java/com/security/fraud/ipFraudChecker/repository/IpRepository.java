package com.security.fraud.ipFraudChecker.repository;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface IpRepository extends ReactiveCrudRepository<IpInfoEntity, Long> {

    // GET BY IP
    Mono<IpInfoEntity> findByIpAddress(String ipAddress);


    // POST

    // UDPATE

    // POST
}
