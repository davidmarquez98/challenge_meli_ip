package com.security.fraud.ipFraudChecker.service;

import com.security.fraud.ipFraudChecker.entity.EstadisticasEntity;
import com.security.fraud.ipFraudChecker.repository.EstadisticasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class EstadisticasService {

    @Autowired
    EstadisticasRepository estadisticasRepository;

    public Mono<Double> getPromedioDistancias(Double estimatedDistance){

        return estadisticasRepository.findAll(Sort.unsorted())
                .collectList()
                .flatMap(estadisticasList -> {
                    EstadisticasEntity estadisticas = null;

                    if (estadisticasList.isEmpty()) {

                        estadisticas = new EstadisticasEntity();
                        estadisticas.setTotalEstimatedDistance(estimatedDistance);
                        estadisticas.setInvocationsCount(1);

                    }else{

                        estadisticas = estadisticasList.getFirst();
                        estadisticas.setTotalEstimatedDistance(estadisticas.getTotalEstimatedDistance() + estimatedDistance);
                        estadisticas.setInvocationsCount(estadisticas.getInvocationsCount() + 1);

                    }

                    return estadisticasRepository.save(estadisticas)
                            .map(nuevasEstadisticas -> nuevasEstadisticas.getInvocationsCount() > 0 ?
                                    nuevasEstadisticas.getTotalEstimatedDistance() / nuevasEstadisticas.getInvocationsCount() : 0.0 );

                });
    }
}
