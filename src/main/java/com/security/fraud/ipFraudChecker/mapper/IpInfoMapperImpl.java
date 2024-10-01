package com.security.fraud.ipFraudChecker.mapper;

import com.security.fraud.ipFraudChecker.dto.IpInfoDTO;
import com.security.fraud.ipFraudChecker.entity.IpInfoEntity;
import com.security.fraud.ipFraudChecker.utils.DistanceCalculator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;

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
                String formattedTime = getFormattedTime(timezone);

                if(i != 0 && timezonesArray.length() == 1 || i + 1 != timezonesArray.length()){
                    formattedTime = formattedTime + " o ";
                }

                fullMessageTimeZone.append(formattedTime);
            }

            ipInfoEntity.setCurrentLocalTime(fullMessageTimeZone.toString());
        }


        if (jsonObject.has("countryCode") && !jsonObject.isNull("countryCode")) {
            String isoCode = jsonObject.getString("countryCode");
            ipInfoEntity.setIsoCode(isoCode);
        }


        if (jsonObject.has("latlng") && !jsonObject.isNull("latlng")) {
            JSONArray latitudLongitudArray = jsonObject.getJSONArray("latlng");
            double latitudPaisIp = latitudLongitudArray.getDouble(0);
            double longitudPaisIp = latitudLongitudArray.getDouble(1);
            double argentinaLat = -34.0;
            double argentinaLon = -64.0;
            String fullMessageDistancia = "";

            double distanciaPaisIp = DistanceCalculator.calculateDistance(latitudPaisIp, longitudPaisIp, argentinaLat, argentinaLon);
            distanciaPaisIp = Math.round(distanciaPaisIp * 100.0) / 100.0;

            fullMessageDistancia = distanciaPaisIp + " kms (" + argentinaLat + ", " + argentinaLon + ") a (" + latitudPaisIp + ", " + longitudPaisIp + ")";
            ipInfoEntity.setEstimatedDistance(fullMessageDistancia);
        }

        if (jsonObject.has("languages")) {
            JSONObject languages = jsonObject.getJSONObject("languages");
            StringBuilder idiomas = new StringBuilder();

            Iterator<String> keys = languages.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String language = languages.getString(key);
                idiomas.append(language);

                if (keys.hasNext()) {
                    idiomas.append(", ");
                }
            }

            String idiomasStr = idiomas.toString();
            ipInfoEntity.setLanguages(idiomasStr);
        }

        if (jsonObject.has("currencies") && !jsonObject.isNull("currencies")) {
            JSONObject currencies = jsonObject.getJSONObject("currencies");

            String currencyCode = currencies.keys().next();
            ipInfoEntity.setCurrency(currencyCode);
        }

        if (jsonObject.has("rates") && !jsonObject.isNull("rates")) {

            String typeCurrency = ipInfoEntity.getCurrency();
            double mountUsd = jsonObject.getJSONObject("rates").getDouble("USD");

            String fullMessageCurrency  = typeCurrency + " (1 " + typeCurrency + " = " + mountUsd + " USD)";

            ipInfoEntity.setCurrency(fullMessageCurrency);
        }

    }

    private static String getFormattedTime(String timeZone) {

        ZoneOffset offset = ZoneOffset.of(timeZone.replace("UTC", ""));

        ZonedDateTime nowInLocal = ZonedDateTime.now(offset);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        String time = nowInLocal.format(formatter);

        String formattedTime = String.format("%s (UTC%s)", time, timeZone.replace("UTC", ""));

        return formattedTime;
    }

    @Override
    public void fromEntityToModel(IpInfoEntity ipInfoEntity, IpInfoDTO ipInfoDTO) {}

}
