package businessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dataRecords.BookingData;
import dataRecords.PassengerData;

public class BookingTest {

    @Test
    public void testGetPassenger() {
        PassengerData passengerData = new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                20,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01");
        BookingData bookingData = new BookingData(passengerData, 100, "1A", 12345);

        Booking booking = new Booking(bookingData);
        PassengerData result = booking.getPassenger();

        assertEquals(passengerData, result);
    }

    @Test
    public void testGetPrice() {
        BookingData bookingData = new BookingData(new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                20,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01"), 100, "1A", 12345);

        Booking booking = new Booking(bookingData);
        int result = booking.getPrice();

        assertEquals(100, result);
    }

    @Test
    public void testGetSeat() {
        BookingData bookingData = new BookingData(new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                20,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01"), 100, "1A", 12345);

        Booking booking = new Booking(bookingData);
        String result = booking.getSeat();

        assertEquals("1A", result);
    }

    @Test
    public void testGetFlightID() {
        BookingData bookingData = new BookingData(new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                20,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01"), 100, "1A", 12345);

        Booking booking = new Booking(bookingData);
        int result = booking.getFlightID();

        assertEquals(12345, result);
    }

    @Test
    public void testGetBookingData() {
        BookingData bookingData = new BookingData(new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                20,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01"), 100, "1A", 12345);

        Booking booking = new Booking(bookingData);
        BookingData result = booking.getBookingData();

        assertEquals(bookingData, result);
    }

}
