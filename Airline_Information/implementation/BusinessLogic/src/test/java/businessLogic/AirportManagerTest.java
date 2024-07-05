package businessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import dataRecords.AirportData;
import persistence.AirportDAO;

public class AirportManagerTest {

    private AirportManager airportManager;

    @Mock
    private AirportDAO airportDAO;

    @Mock
    private DataSource dataSource;

    @BeforeEach
    public void setup() {

        // Setup the mocks
        MockitoAnnotations.openMocks(this);
        airportManager = new AirportManagerImpl();
        airportManager.setAirportDAO(airportDAO);
        airportManager.setDataSource(dataSource);

    }

    @Test
    public void testAdd() throws SQLException {

        // Create new Airport Data
        AirportData airportData = new AirportData("IAT", "City", "Country");

        // Add the airport
        airportManager.add(airportData);

        // Verify that the airport was added to the mocked DataSource
        verify(airportDAO).storeAirport(airportData, dataSource);
    }

    @Test
    public void testAdd_IataNumber() throws SQLException {

        // Create new Airport Data
        AirportData airportData = new AirportData("IA3", "City", "Country");

        // Assert that an exception is thrown when adding the airport
        assertThrows(IllegalArgumentException.class, () -> {
            airportManager.add(airportData);
        });

        verifyNoInteractions(airportDAO);
    }

    @Test
    public void testAdd_CityNumber() throws SQLException {

        // Create new Airport Data
        AirportData airportData = new AirportData("IAT", "City1", "Country");

        // Assert that an exception is thrown when adding the airport
        assertThrows(IllegalArgumentException.class, () -> {
            airportManager.add(airportData);
        });

        verifyNoInteractions(airportDAO);
    }

    @Test
    public void testAdd_CountryNumber() throws SQLException {

        // Create new Airport Data
        AirportData airportData = new AirportData("IAT", "City", "Country1");

        // Assert that an exception is thrown when adding the airport
        assertThrows(IllegalArgumentException.class, () -> {
            airportManager.add(airportData);
        });

        verifyNoInteractions(airportDAO);
    }

    @Test
    public void testAdd_InvalidIata() throws SQLException {

        // Create new Airport Data
        AirportData airportData = new AirportData("Long IATA", "City", "Country");

        // Assert that an exception is thrown when adding the airport
        assertThrows(IllegalArgumentException.class, () -> {
            airportManager.add(airportData);
        });

        verifyNoInteractions(airportDAO);
    }

    @Test
    public void testGetAllAirports() throws SQLException {

        List<AirportData> mockAirportList = new ArrayList<>();
        when(airportManager.getAllAirports()).thenReturn(mockAirportList);

        List<AirportData> result = airportManager.getAllAirports();

        // Verify that the DAO was called and the result is the mock list
        verify(airportDAO).getAllAirports();
        assertEquals(mockAirportList, result);

    }

    @Test
    public void testAddMultipleAirports() throws SQLException {
        // Create two new Airports
        AirportData airport1 = new AirportData("APA", "CityOne", "CountryOne");
        AirportData airport2 = new AirportData("APB", "CityTwo", "CountryTwo");

        // Add them
        airportManager.add(airport1);
        airportManager.add(airport2);

        // Verify that they were added in the correct order
        InOrder inOrder = Mockito.inOrder(airportDAO);
        inOrder.verify(airportDAO).storeAirport(airport1, dataSource);
        inOrder.verify(airportDAO).storeAirport(airport2, dataSource);
    }

    @Test
    public void testGetAllAirportsEmptyList() throws SQLException {
        List<AirportData> emptyList = new ArrayList<>();
        when(airportDAO.getAllAirports()).thenReturn(emptyList);

        List<AirportData> result = airportManager.getAllAirports();

        // Verify that the DAO was called and the result is an empty list
        verify(airportDAO).getAllAirports();
        assertEquals(emptyList, result);
    }

    @Test
    public void testInteractions() throws SQLException {

        // Create new Airport Data
        AirportData airportData = new AirportData("AIR", "City", "Country");

        // Add the Airport Data, then get all airports
        airportManager.add(airportData);
        airportManager.getAllAirports();

        verify(airportDAO).storeAirport(airportData, dataSource);
        verify(airportDAO).getAllAirports();
    }
}
