/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package businessLogic;

import dataRecords.FlightData;
import dataRecords.PlaneData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * @author patrasalexandru
 */
public class Flight {

    private FlightData flightData;

    public Flight(FlightData flightData) {
        this.flightData = flightData;
    }
    public FlightData getFlightData(){
        return this.flightData;
    }
    public int getId() {
        return this.flightData.id();
    }

    public String getDepartureAirport() {
        return this.flightData.departureAirport();
    }

    public String getArrivalAirport() {
        return this.flightData.arrivalAirport();
    }

    public String getGateNr() {
        return this.flightData.gateNr();
    }

    public LocalDate getDepartureDate() {
        return this.flightData.departureDate();
    }

    public LocalDate getArrivalDate() {
        return this.flightData.arrivalDate();
    }

    public LocalTime getDepartureTime() {
        return this.flightData.departureTime();
    }

    public LocalTime getArrivalTime() {
        return this.flightData.arrivalTime();
    }

    public PlaneData getPlaneDetails() {
        return this.flightData.planeData();
    }

    public int getPrice() {
        return this.flightData.price();
    }

    public String getCurrency() {
        return this.flightData.currency();
    }

}
