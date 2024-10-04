package com.security.fraud.ipFraudChecker.repository;

import com.security.fraud.ipFraudChecker.entity.EstadisticasEntity;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EstadisticasRepository extends ReactiveSortingRepository<EstadisticasEntity, Long> {
    Mono<EstadisticasEntity> save(EstadisticasEntity estadisticasEntity);
}
