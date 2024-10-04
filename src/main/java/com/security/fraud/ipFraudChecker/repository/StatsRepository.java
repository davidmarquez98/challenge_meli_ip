package com.security.fraud.ipFraudChecker.repository;

import com.security.fraud.ipFraudChecker.entity.StatsEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StatsRepository extends ReactiveSortingRepository<StatsEntity, Long> {
    Mono<StatsEntity> save(StatsEntity statsEntity);
}
