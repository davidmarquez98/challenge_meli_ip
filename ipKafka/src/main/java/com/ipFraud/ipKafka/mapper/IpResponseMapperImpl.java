package com.ipFraud.ipKafka.mapper;

import com.ipFraud.ipKafka.response.IpInfoResponse;
import com.ipFraud.ipKafka.utils.DistanceCalculator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IpResponseMapperImpl implements IpResponseMapper {

    private static final Map<String, String> timezoneCache = new ConcurrentHashMap<>();

    @Override
    public void fromJsonToResponse(JSONObject jsonObject, IpInfoResponse ipInfoResponse){

        String country = jsonObject.optString("country", null);
        JSONArray timezones = jsonObject.optJSONArray("timezones");
        String countryCode = jsonObject.optString("countryCode", null);
        JSONArray latlng = jsonObject.optJSONArray("latlng");
        JSONObject languages = jsonObject.optJSONObject("languages");
        JSONObject currencies = jsonObject.optJSONObject("currencies");
        JSONObject rates = jsonObject.optJSONObject("rates");

        if (country != null) {
            String pais = jsonObject.getString("country");
            ipInfoResponse.setCountry(pais);
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

            ipInfoResponse.setCurrentLocalTime(fullMessageTimeZone.toString());
        }


        if (countryCode != null) {
            String isoCode = jsonObject.getString("countryCode");
            ipInfoResponse.setIsoCode(isoCode);
        }


        if (latlng != null) {
            JSONArray latitudLongitudArray = jsonObject.getJSONArray("latlng");
            double latitudPaisIp = latitudLongitudArray.getDouble(0);
            double longitudPaisIp = latitudLongitudArray.getDouble(1);
            double argentinaLat = -34.0;
            double argentinaLon = -64.0;

            Double distanciaPaisIp = DistanceCalculator.calculateDistance(latitudPaisIp, longitudPaisIp, argentinaLat, argentinaLon);
            String fullMessageDistancia = String.format("%.2f kms (%f, %f) a (%f, %f)", distanciaPaisIp, argentinaLat, argentinaLon, latitudPaisIp, longitudPaisIp);
            ipInfoResponse.setMessageEstimatedDistance(fullMessageDistancia);
            ipInfoResponse.setEstimatedDistance(distanciaPaisIp);
        }

        if (languages != null) {
            String idiomasStr = languages.toMap().values().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            ipInfoResponse.setLanguages(idiomasStr);
        }

        if (currencies != null) {
            String currencyCode = currencies.keys().next();
            ipInfoResponse.setCurrency(currencyCode);
        }

        if (rates != null) {
            String typeCurrency = ipInfoResponse.getCurrency();
            double mountUsd = jsonObject.getJSONObject("rates").getDouble("USD");

            String fullMessageCurrency  = typeCurrency + " (1 " + typeCurrency + " = " + mountUsd + " USD)";

            ipInfoResponse.setCurrency(fullMessageCurrency);
        }

    }


    private static String getFormattedTime(String timeZone) {
        return timezoneCache.computeIfAbsent(timeZone, tz -> {
            ZoneOffset offset = ZoneOffset.of(tz.replace("UTC", ""));
            ZonedDateTime nowInLocal = ZonedDateTime.now(offset);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return String.format("%s (UTC%s)", nowInLocal.format(formatter), tz.replace("UTC", ""));
        });
    }
}
