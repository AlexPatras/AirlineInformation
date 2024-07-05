package businessLogic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for making API calls, accessing and retrieving weather data at certain location in certain point in time,
 * also getting coordinates of a certain location based on its name
 */
public class APIClientImpl implements APIClient{


    /**
     * Gets weather information (sun hours, clouds, etc.) in a certain location at specific timestamp. Makes an API call to OpenWeatherMap.org
     * which provides all weather data in form of JSON
     * @param lat represents latitude of a certain location in form of String
     * @param lon represents longitude of a certain location in form of String
     * @param date provides a timestamp for API call for when to look for weather
     * @return sun hours of the destination in form of double
     * @throws DiscountNotAppliedException if exception type of DiscountDatabaseException is caught
     */
    @Override
    public double fetchWeather(String lat, String lon, LocalDate date, String discountMode) throws DiscountNotAppliedException {
        double sunHours = -1;
        double clouds = -1;

        LocalDateTime localDateTime = date.atStartOfDay();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        long timestamp = instant.getEpochSecond();

        String unixDateFormat = String.valueOf(timestamp);

        String urlText = "https://api.openweathermap.org/data/3.0/onecall/timemachine?lat=" + lat + "&lon=" + lon + "&dt=" + unixDateFormat + "&appid=5eef0aada2edcbe8e50429f7a5528c14";


        try {
            StringBuilder weather = new StringBuilder();
            URL url = new URL(urlText);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                weather.append(line);
            }
            reader.close();

            Map<String, Object> mainMap = jsonToMap(weather.toString());

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) mainMap.get("data");
            Map<String, Object> data1 = dataList.get(0);
            String sunriseUnixFormatString = data1.get("sunrise").toString();
            String sunsetUnixFormatString = data1.get("sunset").toString();
            String cloudPercentageString = data1.get("clouds").toString();

            double sunriseUnixFormatDouble = Double.parseDouble(sunriseUnixFormatString);
            double sunsetUnixFormatDouble = Double.parseDouble(sunsetUnixFormatString);

            long sunriseUnixFormat = Math.round(sunriseUnixFormatDouble);
            long sunsetUnixFormat = Math.round(sunsetUnixFormatDouble);

            sunHours = betweenInDouble(formatTime(sunriseUnixFormat), formatTime(sunsetUnixFormat));
            clouds = Double.parseDouble(cloudPercentageString);

            if (clouds > 30) {
                clouds = 30;
            } else if (clouds < 10) {
                clouds = 10;
            }

            if (discountMode.equals("Sun hours")) {
                return sunHours;
            } else {
                return clouds;
            }

        } catch (IOException e) {
            throw new DiscountNotAppliedException("Problem occurred while fetching weather data");
        }

    }


    /**
     * Gets coordinates (latitude and longitude) of any place based on city name.
     * Makes an API call to google maps geocoding API to retrieve data. API call returns data in JSON format
     * @param location is the name of city (or more specific location) to be put in API call.
     * @return list of two coordinates in form of double
     * @throws DiscountNotAppliedException if exception type of DiscountDatabaseException is caught
     */
    @Override
    public List<Double> getCoordinates(String location) throws DiscountNotAppliedException {
        ArrayList<Double> coordinates = new ArrayList<>();

        String fixedLocation;
        char firstLetter = location.charAt(0);
        if (Character.isLowerCase(firstLetter)) {
            firstLetter = Character.toUpperCase(firstLetter);
            fixedLocation = firstLetter + location.substring(1);
        } else {
            fixedLocation = location;
        }

        String urlText = "https://maps.googleapis.com/maps/api/geocode/json?address=" + fixedLocation + "&key=AIzaSyDpC9G2mjP06wBarq_sC-NLYY9xaFfjmRs";

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlText);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            Map<String, Object> mainMap = jsonToMap(result.toString());

            List<Map<String, Object>> dataList = (List<Map<String, Object>>) mainMap.get("results");

            double lat = 0;
            double lon = 0;

            if (dataList != null && !dataList.isEmpty()) {
                Map<String, Object> data1 = dataList.get(0);
                Map<String, Object> data2 = (Map<String, Object>) data1.get("geometry");
                Map<String, Double> data3 = (Map<String, Double>) data2.get("location");

                lat = data3.get("lat");
                lon = data3.get("lng");

                if (lat != 0 || lon != 0) {
                    coordinates.add(lat);
                    coordinates.add(lon);
                }

            }

        } catch (IOException e) {
            throw new DiscountNotAppliedException("Problem occurred while fetching location coordinates");
        }

        return coordinates;
    }


    // Helper methods

    /**
     * Converts time from Unix timestamp to LocalTime in hours and seconds
     * @param unixTimeStamp passes the value of Unix timestamp to be converted
     * @return time in hours and seconds in form of LocalTime
     */
    private LocalTime formatTime(long unixTimeStamp) {
        Instant instant = Instant.ofEpochSecond(unixTimeStamp);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        return localDateTime.toLocalTime();
    }

    /**
     * Converts time period from hours and seconds to double format
     * @param start is start of time period
     * @param end is end of time period
     * @return time in form of double
     */
    private double betweenInDouble(LocalTime start, LocalTime end) {
        Duration duration = Duration.between(start, end);
        double seconds = duration.toSeconds();
        double hours = (double) (seconds / 60) / 60;
        hours = Math.round(hours * 100);
        hours = hours / 100;
        return hours;
    }

    /**
     * Helps to store data from JSON form to map with the assistance of GSON
     * @param input is the input from JSON data source
     * @return map of String and Object to store various data objects form JSON source
     */
    private static Map<String, Object> jsonToMap (String input) {
        return new Gson().fromJson(input, new TypeToken<HashMap<String, Object>>() {}.getType());
    }
}
