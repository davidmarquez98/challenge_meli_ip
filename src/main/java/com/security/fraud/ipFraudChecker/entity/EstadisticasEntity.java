package com.security.fraud.ipFraudChecker.entity;

import org.springframework.data.annotation.Id;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "estadisticas")

public class EstadisticasEntity {

    @Id
    private Long id;

    Double distanciaMasLejana;

    Double distanciaMasCercana;

    Double totalDistancias;

    Double totalInvocaciones;

}
