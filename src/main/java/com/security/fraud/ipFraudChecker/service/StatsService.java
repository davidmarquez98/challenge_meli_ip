package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.entity.StatsEntity;
import com.security.fraud.ipFraudChecker.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StatsService {

    @Autowired
    StatsRepository statsRepository;

    public Mono<Double> getAvgDistancias(Double estimatedDistance){

        return statsRepository.findAll(Sort.unsorted())
                .collectList()
                .flatMap(estadisticasList -> {
                    StatsEntity statEntity = null;

                    if (estadisticasList.isEmpty()) {
                        statEntity = new StatsEntity();
                        statEntity.setTotalEstimatedDistance(estimatedDistance);
                        statEntity.setInvocationsCount(1);
                    }else{
                        statEntity = estadisticasList.getFirst();
                        statEntity.setTotalEstimatedDistance(statEntity.getTotalEstimatedDistance() + estimatedDistance);
                        statEntity.setInvocationsCount(statEntity.getInvocationsCount() + 1);
                    }

                    return statsRepository.save(statEntity)
                            .map(newStats -> newStats.getInvocationsCount() > 0 ?
                                    newStats.getTotalEstimatedDistance() / newStats.getInvocationsCount() : 0.0 );

                });
    }
}
