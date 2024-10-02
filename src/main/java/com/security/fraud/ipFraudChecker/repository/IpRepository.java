package com.security.fraud.ipFraudChecker.repository;

import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface IpRepository extends ReactiveSortingRepository<IpInfoEntity, Long> {

    // GET BY IP
    Mono<IpInfoEntity> findByIpAddress(String ipAddress);

    @Query("SELECT MIN(estimated_distance) FROM ip_info")
    Mono<Double> findMinDistance();

    @Query("SELECT MAX(estimated_distance) FROM ip_info")
    Mono<Double> findMaxDistance();

    @Query("SELECT AVG(estimated_distance) FROM ip_info")
    Mono<Double> findAverageDistance();

    Mono<IpInfoEntity> save(IpInfoEntity ipInfoEntity);


}
