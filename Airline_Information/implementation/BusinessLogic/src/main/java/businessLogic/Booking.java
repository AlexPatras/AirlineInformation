package businessLogic;

import dataRecords.BookingData;
import dataRecords.PassengerData;

public class Booking {
    private BookingData bookingData;

    public Booking(BookingData bookingData) {
        this.bookingData = bookingData;
    }

    public PassengerData getPassenger() {
        return this.bookingData.passengerData();
    }

    public int getPrice() {
        return this.bookingData.price();
    }

    public String getSeat() {
        return this.bookingData.seat();
    }

    public int getFlightID() {
        return this.bookingData.flightID();
    }

    public BookingData getBookingData() {
        return this.bookingData;
    }
}
