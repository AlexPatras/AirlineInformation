package businessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dataRecords.PassengerData;

public class PassengerTest {
    @Test
    public void testGetFirstName() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        String result = passenger.getFirstName();

        assertEquals("John", result);
    }

    @Test
    public void testGetLastName() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        String result = passenger.getLastName();

        assertEquals("Doe", result);
    }

    @Test
    public void testGetEmail() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        String result = passenger.getEmail();

        assertEquals("john.doe@example.com", result);
    }

    @Test
    public void testGetPassengerId() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        int result = passenger.getPassengerId();

        assertEquals(12345, result);
    }

    @Test
    public void testGetPhoneNumber() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        String result = passenger.getPhoneNumber();

        assertEquals("1234567890", result);
    }

    @Test
    public void testGetNationality() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        String result = passenger.getNationality();

        assertEquals("USA", result);
    }

    @Test
    public void testGetBirthDay() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        String result = passenger.getBirthDay();

        assertEquals("1990-01-01", result);
    }

    @Test
    public void testSetPassengerId() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        Passenger updatedData = passenger.setPassengerId(54321);

        assertEquals(54321, updatedData.getPassengerId());
    }

    @Test
    public void testGetPassengerData() {
        PassengerData passengerData = new PassengerData("John", "Doe", "john.doe@example.com", 12345, "1234567890",
                "USA", "ABCD1234", "1990-01-01");

        Passenger passenger = new Passenger(passengerData);
        PassengerData result = passenger.getPassengerData();

        assertEquals(passengerData, result);
    }

}
