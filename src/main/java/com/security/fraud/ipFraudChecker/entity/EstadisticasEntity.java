package com.security.fraud.ipFraudChecker.entity;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@Table(name = "estadisticas")
public class EstadisticasEntity {

    @Id
    private Long id;

    @Column("total_estimated_distance")
    private Double totalEstimatedDistance;

    @Column("invocations_count")
    private int invocationsCount;

}
