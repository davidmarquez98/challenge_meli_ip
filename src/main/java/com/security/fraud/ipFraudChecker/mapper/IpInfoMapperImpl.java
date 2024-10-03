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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IpInfoMapperImpl implements IpInfoMapper{

    @Override
    public void fromJsonToEntity(JSONObject jsonObject, IpInfoEntity ipInfoEntity) {

        String country = jsonObject.optString("country", null);
        JSONArray timezones = jsonObject.optJSONArray("timezones");
        String countryCode = jsonObject.optString("countryCode", null);
        JSONArray latlng = jsonObject.optJSONArray("latlng");
        JSONObject languages = jsonObject.optJSONObject("languages");
        JSONObject currencies = jsonObject.optJSONObject("currencies");
        JSONObject rates = jsonObject.optJSONObject("rates");

        if (country != null) {
            String pais = jsonObject.getString("country");
            ipInfoEntity.setCountry(pais);
        }


        if (timezones != null) {
            StringBuilder fullMessageTimeZone = new StringBuilder();

            for (int i = 0; i < timezones.length(); i++) {
                String timezone = timezones.getString(i);
                fullMessageTimeZone.append(getFormattedTime(timezone));

                if (i + 1 < timezones.length()) {
                    fullMessageTimeZone.append(" o ");
                }
            }

            ipInfoEntity.setCurrentLocalTime(fullMessageTimeZone.toString());
        }


        if (countryCode != null) {
            String isoCode = jsonObject.getString("countryCode");
            ipInfoEntity.setIsoCode(isoCode);
        }


        if (latlng != null) {
            JSONArray latitudLongitudArray = jsonObject.getJSONArray("latlng");
            double latitudPaisIp = latitudLongitudArray.getDouble(0);
            double longitudPaisIp = latitudLongitudArray.getDouble(1);
            double argentinaLat = -34.0;
            double argentinaLon = -64.0;

//            String fullMessageDistancia = "";
//
//            double distanciaPaisIp = DistanceCalculator.calculateDistance(latitudPaisIp, longitudPaisIp, argentinaLat, argentinaLon);
//            distanciaPaisIp = Math.round(distanciaPaisIp * 100.0) / 100.0;
//
//            fullMessageDistancia = distanciaPaisIp + " kms (" + argentinaLat + ", " + argentinaLon + ") a (" + latitudPaisIp + ", " + longitudPaisIp + ")";
//            ipInfoEntity.setEstimatedDistance(fullMessageDistancia);

            Double distanciaPaisIp = DistanceCalculator.calculateDistance(latitudPaisIp, longitudPaisIp, argentinaLat, argentinaLon);
            String fullMessageDistancia = String.format("%.2f kms (%f, %f) a (%f, %f)", distanciaPaisIp, argentinaLat, argentinaLon, latitudPaisIp, longitudPaisIp);
            ipInfoEntity.setMessageEstimatedDistance(fullMessageDistancia);
            ipInfoEntity.setEstimatedDistance(distanciaPaisIp);
        }

        if (languages != null) {
            String idiomasStr = languages.toMap().values().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            ipInfoEntity.setLanguages(idiomasStr);
//            JSONObject languages = jsonObject.getJSONObject("languages");
//            StringBuilder idiomas = new StringBuilder();
//
//            Iterator<String> keys = languages.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                String language = languages.getString(key);
//                idiomas.append(language);
//
//                if (keys.hasNext()) {
//                    idiomas.append(", ");
//                }
//            }
//
//            String idiomasStr = idiomas.toString();
//            ipInfoEntity.setLanguages(idiomasStr);
        }

        if (currencies != null) {
            String currencyCode = currencies.keys().next();
            ipInfoEntity.setCurrency(currencyCode);
        }

        if (rates != null) {
            String typeCurrency = ipInfoEntity.getCurrency();
            double mountUsd = jsonObject.getJSONObject("rates").getDouble("USD");

            String fullMessageCurrency  = typeCurrency + " (1 " + typeCurrency + " = " + mountUsd + " USD)";

            ipInfoEntity.setCurrency(fullMessageCurrency);
        }

    }

    private static final Map<String, String> timezoneCache = new ConcurrentHashMap<>();

    private static String getFormattedTime(String timeZone) {
        return timezoneCache.computeIfAbsent(timeZone, tz -> {
            ZoneOffset offset = ZoneOffset.of(tz.replace("UTC", ""));
            ZonedDateTime nowInLocal = ZonedDateTime.now(offset);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return String.format("%s (UTC%s)", nowInLocal.format(formatter), tz.replace("UTC", ""));
        });
    }

    @Override
    public void fromEntityToModel(IpInfoEntity ipInfoEntity, IpInfoDTO ipInfoDTO) {}

}
