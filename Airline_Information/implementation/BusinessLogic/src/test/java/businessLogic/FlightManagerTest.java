/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package businessLogic;

import dataRecords.AirportValidationException;
import dataRecords.DateTimeValidationException;
import dataRecords.FlightData;
import dataRecords.FlightDetailsValidationException;
import dataRecords.PlaneData;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import persistence.FlightDAO;

import persistence.FlightDAOImpl;
import persistence.FlightNonDAO;
import persistence.FlightPersistenceException;

/**
 *
 * @author patrasalexandru
 */
public class FlightManagerTest {

    @Mock
    private FlightDAOImpl flightDAO;

    @Mock
    private FlightNonDAO flightNonDAO;

    @Mock
    private DataSource dataSource;

    private FlightManager flightManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        flightManager = new FlightManager();
        flightManager.setFlightNonDao(flightNonDAO);
        flightManager.setFlightDAO(flightDAO);
    }

    @Test
    public void testGetIATACode() throws FlightPersistenceException {
        ArrayList<String> expectedIATACodes = new ArrayList<>();
        expectedIATACodes.add("EIN");
        expectedIATACodes.add("OTP");
        expectedIATACodes.add("DRT");

        when(flightNonDAO.getIATACode()).thenReturn(expectedIATACodes);

        ArrayList<String> iataCodes = flightManager.getIATACode();

        verify(flightNonDAO).getIATACode();
        assertEquals(expectedIATACodes, iataCodes);
    }

    @Test
    public void testGetIATACodeThrowsException() throws FlightPersistenceException {
        ArrayList<String> expectedIATACodes = new ArrayList<>();
        expectedIATACodes.add("OTP");
        expectedIATACodes.add("DRT");

        when(flightNonDAO.getIATACode()).thenThrow(new FlightPersistenceException("Couldn't get IATA codes"));

        assertThrows(FlightPersistenceException.class, () -> {
            flightManager.getIATACode();
        });
    }

    @Test
    public void testGetPlaneDetails() throws FlightPersistenceException {

        ArrayList<String> expectedDetails = new ArrayList<>();
        expectedDetails.add("AirbusABC - 200");
        expectedDetails.add("AirbusA1  - 150");
        expectedDetails.add("AirbusA   - 100");

        when(flightNonDAO.getPlaneDetails()).thenReturn(expectedDetails);

        ArrayList<String> planeDetails = flightManager.getPlaneDetails();

        verify(flightNonDAO).getPlaneDetails();
        assertEquals(expectedDetails, planeDetails);

        for (String detail : planeDetails) {
            String[] parts = detail.split(" - ");
            assertEquals(2, parts.length);

            assertTrue(parts[1].matches("\\d+"));
        }
    }

    @Test
    public void testGetPlaneDetailsThrowsException() throws FlightPersistenceException {
        ArrayList<String> expectedDetails = new ArrayList<>();
        expectedDetails.add("AirbusA - 100");
        expectedDetails.add("AirbusB  - 150");
        expectedDetails.add("AirbusC   - 200");

        when(flightNonDAO.getPlaneDetails()).thenThrow(new FlightPersistenceException("Couldn't get model and number of seats"));
        assertThrows(FlightPersistenceException.class, () -> {
            flightManager.getPlaneDetails();
        });
    }

    @Test
    public void createFlight() throws Exception {
        PlaneData planeData = new PlaneData(1, "AirbusA380", 150);
        FlightData flightData = new FlightData(
                1,
                "AMS",
                "IST",
                LocalDate.of(2023, 07, 18),
                LocalDate.of(2023, 07, 18),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("15:00:00"),
                200,
                "EUR",
                "4",
                planeData);
        Flight flight = new Flight(flightData);

        doNothing().when(flightDAO).create(flightData);
        flightManager.create(flight);

        verify(flightDAO, times(1)).create(flightData);

    }

    @Test
    public void createFlightThrowsException() throws FlightPersistenceException {

        PlaneData planeData = new PlaneData(1, "AirbusB", 150);
        FlightData flightData = new FlightData(
                1,
                "DRT",
                "EIN",
                LocalDate.of(2023, 9, 12),
                LocalDate.of(2023, 9, 12),
                LocalTime.parse("15:00:00"),
                LocalTime.parse("16:25:00"),
                120,
                "EUR",
                "11",
                planeData);
        Flight flight = new Flight(flightData);

        doThrow(new FlightPersistenceException("")).when(flightDAO).create(flightData);
        assertThrows(FlightPersistenceException.class, () -> {

            flightManager.create(flight);

        });
        
        verify(flightDAO,times(1)).create(flightData);
    }
    
    @Test
    public void checkRegisteredFlight() throws Exception {
        PlaneData planeData = new PlaneData(1, "AirbusA380", 150);
        FlightData flightData = new FlightData(
                1,
                "AMS",
                "IST",
                LocalDate.of(2023, 07, 18),
                LocalDate.of(2023, 07, 18),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("15:00:00"),
                200,
                "EUR",
                "4",
                planeData);

        when(flightNonDAO.CheckRegisterFlights(flightData, dataSource)).thenReturn(true);
        boolean result = flightManager.CheckRegisterFlights(flightData, dataSource);

        assertTrue(result);
        verify(flightNonDAO).CheckRegisterFlights(flightData, dataSource);

    }

    @Test
    public void checkRegisterFlightThrowsException() throws FlightPersistenceException {
        PlaneData planeData = new PlaneData(1, "AirbusA380", 150);
        FlightData flightData = new FlightData(
                1,
                "AMS",
                "IST",
                LocalDate.of(2023, 07, 18),
                LocalDate.of(2023, 07, 18),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("15:00:00"),
                200,
                "EUR",
                "4",
                planeData);

        when(flightNonDAO.CheckRegisterFlights(flightData, dataSource)).thenThrow(new FlightPersistenceException("Error checking flight existence"));
        assertThrows(FlightPersistenceException.class, () -> {
            flightManager.CheckRegisterFlights(flightData, dataSource);
        });

        verify(flightNonDAO).CheckRegisterFlights(flightData, dataSource);
    }
    @Test
    public void testInvalidTime() {
        PlaneData planeData = new PlaneData(1, "AirbusA380", 150);
        FlightData flightData = new FlightData(
                1,
                "OTP",
                "DRT",
                LocalDate.of(2023, 06, 15),
                LocalDate.of(2023, 06, 15),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("08:00:00"),
                150,
                "EUR",
                "12",
                planeData);

        Flight flight = new Flight(flightData);

        DateTimeValidationException exception = assertThrows(DateTimeValidationException.class,
                () -> flightManager.create(flight));
    }

    @Test
    public void testInvalidAirport() {
        PlaneData planeData = new PlaneData(1, "Airbus-A20", 230);
        FlightData flightData = new FlightData(
                1,
                "OTP",
                "OTP",
                LocalDate.of(2023, 8, 12),
                LocalDate.of(2023, 8, 12),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("15:25:00"),
                120,
                "EUR",
                "12",
                planeData);

        Flight flight = new Flight(flightData);

        AirportValidationException exception = assertThrows(AirportValidationException.class,
                () -> flightManager.create(flight));

    }

    @Test
    public void testInvalidGateNumberFormat() {
        PlaneData planeData = new PlaneData(1, "Airbus-A20", 230);
        FlightData flightData = new FlightData(
                1,
                "OTP",
                "EIN",
                LocalDate.of(2023, 8, 12),
                LocalDate.of(2023, 8, 12),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("15:25:00"),
                120,
                "EUR",
                "C",
                planeData);

        Flight flight = new Flight(flightData);

        FlightDetailsValidationException exception = assertThrows(FlightDetailsValidationException.class,
                () -> flightManager.create(flight));

    }

    @Test
    public void testInvalidGateNumberLength() {
        PlaneData planeData = new PlaneData(1, "Airbus-A20", 230);
        FlightData flightData = new FlightData(
                1,
                "OTP",
                "EIN",
                LocalDate.of(2023, 8, 12),
                LocalDate.of(2023, 8, 12),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("15:25:00"),
                120,
                "EUR",
                "22222",
                planeData);

        Flight flight = new Flight(flightData);

        FlightDetailsValidationException exception = assertThrows(FlightDetailsValidationException.class,
                () -> flightManager.create(flight));

    }

    @Test
    public void testInvalidDate() {
        PlaneData planeData = new PlaneData(1, "Airbus-A20", 230);
        FlightData flightData = new FlightData(
                1,
                "OTP",
                "EIN",
                LocalDate.of(2023, 10, 20),
                LocalDate.of(2023, 8, 12),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("15:25:00"),
                120,
                "EUR",
                "2",
                planeData);

        Flight flight = new Flight(flightData);

        DateTimeValidationException exception = assertThrows(DateTimeValidationException.class,
                () -> flightManager.create(flight));
    }

    

    @Test
    public void testInvalidPrice() {
        PlaneData planeData = new PlaneData(1, "AirbusA380", 150);
        FlightData flightData = new FlightData(
                1,
                "OTP",
                "DRT",
                LocalDate.of(2023, 06, 15),
                LocalDate.of(2023, 06, 15),
                LocalTime.parse("11:00:00"),
                LocalTime.parse("21:00:00"),
                50000,
                "EUR",
                "12",
                planeData);

        Flight flight = new Flight(flightData);
        FlightDetailsValidationException exception = assertThrows(FlightDetailsValidationException.class,
                () -> flightManager.create(flight));
    }
}
