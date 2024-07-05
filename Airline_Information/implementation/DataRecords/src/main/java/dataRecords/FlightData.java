package dataRecords;

import java.time.LocalDate;
import java.time.LocalTime;

public record FlightData(int id,String departureAirport, String arrivalAirport,LocalDate departureDate, LocalDate arrivalDate, LocalTime departureTime, LocalTime arrivalTime, int price, String currency, String gateNr, PlaneData planeData){}
