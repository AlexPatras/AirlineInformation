 package businessLogic;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

 import java.time.*;
 import java.util.List;

 import static com.github.tomakehurst.wiremock.client.WireMock.*;
 import static org.assertj.core.api.Assertions.assertThat;
 import static org.assertj.core.api.Assertions.assertThatCode;

 public class APIClientTest {

     private APIClient apiClient;
     private static WireMockServer wireMockServer;

     @BeforeEach
     public void setup() {
         this.apiClient = new APIClientImpl();
         wireMockServer = new WireMockServer();
         wireMockServer.start();
         WireMock.configureFor("localhost", wireMockServer.port());
     }

     @Test
     public void fetchesWeatherAsExpected() throws DiscountNotAppliedException {
         String latitude = "41.902782";
         String longitude = "12.496366";
         String discountMode = "Sun hours";

         long timestamp =  1685959200;

         Instant instant = Instant.ofEpochSecond(timestamp);
         ZoneId zoneId = ZoneId.systemDefault();
         ZonedDateTime zonedDateTime = instant.atZone(zoneId);
         LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
         LocalDate resultDate = localDateTime.toLocalDate();
         String unixDateFormat = String.valueOf(timestamp);

        wireMockServer.stubFor(get(urlEqualTo("https://api.openweathermap.org/data/3.0/onecall/timemachine?lat=" + latitude + "&lon=" + longitude + "&dt=" + unixDateFormat + "&appid=5eef0aada2edcbe8e50429f7a5528c14"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\n" +
                                "    \"lat\": 41.9028,\n" +
                                "    \"lon\": 12.4964,\n" +
                                "    \"timezone\": \"Europe/Rome\",\n" +
                                "    \"timezone_offset\": 7200,\n" +
                                "    \"data\": [{\n" +
                                "        \"dt\": 1685110531,\n" +
                                "        \"sunrise\": 1685072470,\n" +
                                "        \"sunset\": 1685125991,\n" +
                                "        \"temp\": 302.42,\n" +
                                "        \"feels_like\": 302.13,\n" +
                                "        \"pressure\": 1013,\n" +
                                "        \"humidity\": 41,\n" +
                                "        \"dew_point\": 287.82,\n" +
                                "        \"uvi\": 4.82,\n" +
                                "        \"clouds\": 20,\n" +
                                "        \"visibility\": 10000,\n" +
                                "        \"wind_speed\": 5.66,\n" +
                                "        \"wind_deg\": 280,\n" +
                                "        \"weather\": [{\n" +
                                "            \"id\": 801,\n" +
                                "            \"main\": \"Clouds\",\n" +
                                "            \"description\": \"few clouds\",\n" +
                                "            \"icon\": \"02d\"\n" +
                                "        }]\n" +
                                "    }]\n" +
                                "}")));
        double expectedSunHours = 15.08;

         double result = apiClient.fetchWeather(latitude, longitude, resultDate, discountMode);
         assertThat(result).isEqualTo(expectedSunHours);
     }

     @Test
     public void fetchingWeatherDoesNotThrowException() {
         String latitude = "41.902782";
         String longitude = "12.496366";
         String discountMode = "Sun hours";

         LocalDateTime localDateTime = LocalDateTime.now();
         LocalDate resultDate = localDateTime.toLocalDate();
         ThrowableAssert.ThrowingCallable code = () -> {
             apiClient.fetchWeather(latitude, longitude, resultDate, discountMode);
         };

         assertThatCode(code).doesNotThrowAnyException();
     }


     @Test
     public void getsCoordinatesAsExpected() throws DiscountNotAppliedException {
         String location = "Rome";
         wireMockServer.stubFor(get(urlEqualTo("get(urlEqualTo(\"https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=AIzaSyDpC9G2mjP06wBarq_sC-NLYY9xaFfjmRs"))
                 .willReturn(aResponse()
                         .withStatus(200)
                         .withBody("{\n" +
                                 "    \"results\": [{\n" +
                                 "        \"address_components\": [{\n" +
                                 "                \"long_name\": \"Rome\",\n" +
                                 "                \"short_name\": \"Rome\",\n" +
                                 "                \"types\": [\"locality\", \"political\"]\n" +
                                 "            },\n" +
                                 "            {\n" +
                                 "                \"long_name\": \"Rome\",\n" +
                                 "                \"short_name\": \"Rome\",\n" +
                                 "                \"types\": [\"administrative_area_level_3\", \"political\"]\n" +
                                 "            },\n" +
                                 "            {\n" +
                                 "                \"long_name\": \"Metropolitan City of Rome Capital\",\n" +
                                 "                \"short_name\": \"RM\",\n" +
                                 "                \"types\": [\"administrative_area_level_2\", \"political\"]\n" +
                                 "            },\n" +
                                 "            {\n" +
                                 "                \"long_name\": \"Lazio\",\n" +
                                 "                \"short_name\": \"Lazio\",\n" +
                                 "                \"types\": [\"administrative_area_level_1\", \"political\"]\n" +
                                 "            },\n" +
                                 "            {\n" +
                                 "                \"long_name\": \"Italy\",\n" +
                                 "                \"short_name\": \"IT\",\n" +
                                 "                \"types\": [\"country\", \"political\"]\n" +
                                 "            }\n" +
                                 "        ],\n" +
                                 "        \"formatted_address\": \"Rome, Metropolitan City of Rome Capital, Italy\",\n" +
                                 "        \"geometry\": {\n" +
                                 "            \"bounds\": {\n" +
                                 "                \"northeast\": {\n" +
                                 "                    \"lat\": 42.0505462,\n" +
                                 "                    \"lng\": 12.7302888\n" +
                                 "                },\n" +
                                 "                \"southwest\": {\n" +
                                 "                    \"lat\": 41.769596,\n" +
                                 "                    \"lng\": 12.341707\n" +
                                 "                }\n" +
                                 "            },\n" +
                                 "            \"location\": {\n" +
                                 "                \"lat\": 41.9027835,\n" +
                                 "                \"lng\": 12.4963655\n" +
                                 "            },\n" +
                                 "            \"location_type\": \"APPROXIMATE\",\n" +
                                 "            \"viewport\": {\n" +
                                 "                \"northeast\": {\n" +
                                 "                    \"lat\": 42.0505462,\n" +
                                 "                    \"lng\": 12.7302888\n" +
                                 "                },\n" +
                                 "                \"southwest\": {\n" +
                                 "                    \"lat\": 41.769596,\n" +
                                 "                    \"lng\": 12.341707\n" +
                                 "                }\n" +
                                 "            }\n" +
                                 "        },\n" +
                                 "        \"place_id\": \"ChIJu46S-ZZhLxMROG5lkwZ3D7k\",\n" +
                                 "        \"types\": [\"locality\", \"political\"]\n" +
                                 "    }],\n" +
                                 "    \"status\": \"OK\"\n" +
                                 "}")));


         double expectedLatitude = 41.9027835;
         double expectedLongitude = 12.4963655;

         List<Double> resultCoordinates = apiClient.getCoordinates(location);

         assertThat(resultCoordinates.get(0)).isEqualTo(expectedLatitude);
         assertThat(resultCoordinates.get(1)).isEqualTo(expectedLongitude);

     }

     @Test
     public void gettingCoordinatesDoesNotThrowAnyException() {
         String location = "Rome";

         ThrowableAssert.ThrowingCallable code = () -> {
             apiClient.getCoordinates(location);
         };

         assertThatCode(code).doesNotThrowAnyException();

     }

    @AfterEach
    public void cleanUp() {
        wireMockServer.stop();
    }
 }
