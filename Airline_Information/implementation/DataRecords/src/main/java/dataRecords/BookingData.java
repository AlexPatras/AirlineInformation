package dataRecords;

public record BookingData(PassengerData passengerData, int price, String seat, int flightID) {}
