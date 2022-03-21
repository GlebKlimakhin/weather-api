package com.glebklim.weatherapi.api;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
@Getter
@Setter
public class TestService {

    @Autowired
    private RestTemplate restTemplate;

    private JsonElement currentCountryCode;
    private JsonElement currentCity;

    @PostConstruct
    @SneakyThrows
    private void setLocation() throws LocationNotFoundException {
        Gson gson = new Gson();
        HttpEntity<Object> httpEntity = new HttpEntity<>(prepareAddressHeaders());
        String ipAddress = getIpAddress();
        String address = String.format("http://api.ipstack.com/%s?access_key=02181e03b7b0fd9a957e7e5b9a35ce7f", ipAddress);
        httpEntity = restTemplate.exchange(address,
                HttpMethod.GET,
                httpEntity,
                Object.class);
        String stringResponse = gson.toJson(httpEntity.getBody());
        JsonObject jsonResponse = new Gson().fromJson(stringResponse, JsonObject.class);
        setCurrentCountryCode(jsonResponse.get("country_code"));
        setCurrentCity(jsonResponse.get("city"));
        if (currentCity == null || currentCountryCode == null) {
            throw new LocationNotFoundException("We cannot determine your location");
        }
    }

    public String getCurrentWeatherForYourLocation() {
        HttpEntity<String> httpEntity = new HttpEntity<>(prepareWeatherHeaders());
        String address = String.format("https://community-open-weather-map.p.rapidapi.com/weather?q=%s&lang=ru&units=imperial&mode=xml", getCurrentCity().getAsString() + "," + getCurrentCountryCode().getAsString());
        httpEntity = restTemplate.exchange(address,
                HttpMethod.GET,
                httpEntity,
                String.class);
        JSONObject xmlJsonObject = XML.toJSONObject(httpEntity.getBody());
        String jsonString = xmlJsonObject.toString();
        return jsonString;
    }

//    public String getCurrentWeather(String countryCode, String city) {
//        Http
//    }


    //utility
    private HttpHeaders prepareAddressHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private HttpHeaders prepareWeatherHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com");
        headers.add("x-rapidapi-key", "29758fd905msh31e1f5fb7ca0f06p1fecd9jsn53843a0e83bb");
        headers.add("Content-Type", "application/xml; charset=utf-8");
        return headers;
    }

    private String getIpAddress() {
        String url = "http://checkip.amazonaws.com/";
        HttpEntity<?> httpEntity = new HttpEntity<>(prepareAddressHeaders());
        httpEntity = restTemplate.exchange(url,
                HttpMethod.GET,
                httpEntity,
                String.class);
        return httpEntity.getBody().toString().replaceAll("\n", "");
    }
}