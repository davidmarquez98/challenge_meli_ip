package com.security.fraud.ipFraudChecker.mapper;

import com.security.fraud.ipFraudChecker.dto.IpInfoDTO;
import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class IpInfoMapperImpl implements IpInfoMapper{

    @Override
    public void fromJsonToEntity(JSONObject jsonObject, IpInfoEntity ipInfoEntity) {

        if (jsonObject.has("country") && !jsonObject.isNull("country")) {
            String pais = jsonObject.getString("country");
            ipInfoEntity.setCountry(pais);
        }

        if (jsonObject.has("timezones") && !jsonObject.isNull("timezones")) {
            JSONArray timezonesArray = jsonObject.getJSONArray("timezones");
            StringBuilder fullMessageTimeZone = new StringBuilder();

            for (int i = 0; i < timezonesArray.length(); i++) {
                String timezone = timezonesArray.getString(i);

                // Obtener el tiempo formateado
                String formattedTime = getFormattedTime(timezone);

                if(i == 0 && timezonesArray.length() == 1 || i + 1 != timezonesArray.length()){
                    formattedTime = formattedTime + " o ";
                }

                fullMessageTimeZone.append(formattedTime);
                // Mostrar el resultado
                System.out.println("Formatted Time for " + timezone + ": " + formattedTime);
            }

            ipInfoEntity.setCurrentLocalTime(fullMessageTimeZone.toString());
            // Mostrar el resultado
            System.out.println("Formatted Time: " + fullMessageTimeZone);
        }


    }

    private static String getFormattedTime(String timeZone) {
        // Extraer el desplazamiento
        ZoneOffset offset = ZoneOffset.of(timeZone.replace("UTC", ""));

        // Obtener la hora actual en la zona horaria especificada
        ZonedDateTime nowInLocal = ZonedDateTime.now(offset);

        // Formatear la salida en el formato deseado
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = nowInLocal.format(formatter);

        // Crear la representación final con el formato correcto
        // Ahora solo incluimos el formato que deseas
        String formattedTime = String.format("%s (UTC%s)", time, timeZone.replace("UTC", ""));

        return formattedTime;
    }

    @Override
    public void fromEntityToModel(IpInfoEntity ipInfoEntity, IpInfoDTO ipInfoDTO) {

    }

}
