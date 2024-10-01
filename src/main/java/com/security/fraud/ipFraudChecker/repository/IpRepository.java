package com.security.fraud.ipFraudChecker.repository;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface IpRepository extends R2dbcRepository<IpInfoEntity, Long> {

    // GET BY IP
    Mono<IpInfoEntity> findByIpAddress(String ipAddress);


    // POST

    // UDPATE

    // POST
}
