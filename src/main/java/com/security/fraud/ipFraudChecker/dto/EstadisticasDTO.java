package com.security.fraud.ipFraudChecker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadisticasDTO {

    Double distanciaMasLejana;

    Double distanciaMasCercana;

    Double totalDistancias;

    Double totalInvocaciones;

}
