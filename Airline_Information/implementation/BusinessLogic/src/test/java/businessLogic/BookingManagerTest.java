package businessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dataRecords.BookingData;
import dataRecords.PassengerData;
import persistence.BookingDAO;
import persistence.BookingPersistenceException;
import persistence.FlightNonDAO;
import persistence.FlightPersistenceException;
import persistence.InputValidationException;
import persistence.PassengerNonDAO;
import persistence.PassengerPersistenceException;
import persistence.PlaneNonDAO;
import persistence.PlanePersistenceException;

public class BookingManagerTest {

    @Mock
    private BookingDAO bookingDao;

    private PlaneNonDAO planeNonDAO;

    private FlightNonDAO flightNonDAO;

    private PassengerNonDAO passengerNonDAO;

    private BookingManager bookingManager;

    private PlaneManager planeManager;

    private FlightManager flightManager;

    private PassengerManager passengerManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        bookingManager = new BookingManager();
        planeManager = new PlaneManager();
        flightManager = new FlightManager();
        passengerManager = new PassengerManager();

        bookingDao = mock(BookingDAO.class);
        planeNonDAO = mock(PlaneNonDAO.class);
        flightNonDAO = mock(FlightNonDAO.class);
        passengerNonDAO = mock(PassengerNonDAO.class);

        ((BookingManager) bookingManager).setBookingDAO(bookingDao);
        ((PlaneManager) planeManager).setPlaneNonDao(planeNonDAO);
        ((FlightManager) flightManager).setFlightNonDao(flightNonDAO);
        ((PassengerManager) passengerManager).setPassengerNonDao(passengerNonDAO);
    }

    @Test
    public void testCreateBooking()
            throws Exception {
        PassengerData passengerData = new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                1,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01");

        BookingData bookingData = new BookingData(
                passengerData,
                100,
                "1A",
                1);
        
        Booking booking = new Booking(bookingData);

        doNothing().when(bookingDao).create(bookingData);

        bookingManager.create(booking);

        verify(bookingDao, times(1)).create(bookingData);

    }

    @Test
    public void testCreateBookingBookingPersistenceException()
            throws Exception {
        PassengerData passengerData = new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                123456,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01");

        BookingData bookingData = new BookingData(
                passengerData,
                100,
                "1A",
                123);

        Booking booking = new Booking(bookingData);

        doThrow(new BookingPersistenceException("")).when(bookingDao).create(bookingData);

        Executable executable = () -> bookingManager.create(booking);

        Assertions.assertThrows(BookingPersistenceException.class, executable);
    }

    @Test
    public void testCreateBookingEmptyFlightId() {
        PassengerData passengerData = new PassengerData(
                "John",
                "Doe",
                "john.doe@example.com",
                123456,
                "123456789",
                "USA",
                "ABCD1234",
                "1990-01-01");

        BookingData bookingData = new BookingData(
                passengerData,
                100,
                "1A",
                0);

        Booking booking = new Booking(bookingData);

        InputValidationException exception = assertThrows(InputValidationException.class,
                () -> bookingManager.create(booking));
        assertEquals("Provide the flight id.", exception.getMessage());
    }

    @Test
    public void testGetOccupiedSeats() throws PlanePersistenceException {
        ArrayList<String> occupiedSeats = new ArrayList<>();
        occupiedSeats.add("1A");
        occupiedSeats.add("3B");

        when(planeNonDAO.getOccupiedSeats()).thenReturn(occupiedSeats);

        ArrayList<String> result = planeManager.getOccupiedSeats();

        verify(planeNonDAO).getOccupiedSeats();

        assertEquals(occupiedSeats, result);
    }

    @Test
    public void testGetOccupiedSeatsPlanePersistenceException()
            throws PlanePersistenceException {
        doThrow(new PlanePersistenceException("")).when(planeNonDAO).getOccupiedSeats();

        Executable executable = () -> planeManager.getOccupiedSeats();

        assertThrows(PlanePersistenceException.class, executable);
    }

    @Test
    public void testGetArrivalIATACode() throws FlightPersistenceException {
        int flightId = 1;
        String expectedIATACode = "DTM";

        when(flightNonDAO.getArrivalIATACode(flightId)).thenReturn(expectedIATACode);

        String result = flightManager.getArrivalIATACode(flightId);

        verify(flightNonDAO).getArrivalIATACode(flightId);

        assertEquals(expectedIATACode, result);
    }

    @Test
    public void testGetArrivalIATACodeFlightPersistenceException()
            throws FlightPersistenceException {
        int flightId = 1;

        doThrow(FlightPersistenceException.class).when(flightNonDAO).getArrivalIATACode(flightId);

        Executable executable = () -> flightManager.getArrivalIATACode(flightId);

        assertThrows(FlightPersistenceException.class, executable);
    }

    @Test
    public void testGetNextFreeId() throws PassengerPersistenceException {
        int expectedId = 1;

        when(passengerNonDAO.getNextFreeId()).thenReturn(expectedId);

        int result = passengerManager.getNextFreeId();

        verify(passengerNonDAO).getNextFreeId();

        assertEquals(expectedId, result);
    }

    @Test
    public void testGetNextFreeIdPassengerPersistenceException()
            throws PassengerPersistenceException {
        doThrow(PassengerPersistenceException.class).when(passengerNonDAO).getNextFreeId();

        Executable executable = () -> passengerManager.getNextFreeId();

        assertThrows(PassengerPersistenceException.class, executable);
    }
}
