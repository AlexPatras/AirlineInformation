package persTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dataRecords.BookingData;
import dataRecords.PassengerData;
import persistence.BookingDAO;
import persistence.BookingPersistenceException;
import persistence.InputValidationException;

public class BookingTest {
    @Mock
    private DataSource db;

    @Mock
    private Connection conn;

    @Mock
    private Statement stm;

    @Mock
    private ResultSet rs;

    private BookingDAO bookingDAO;

    @BeforeEach
    public void setup() throws SQLException {
        MockitoAnnotations.openMocks(this);

        bookingDAO = new BookingDAO();

        when(db.getConnection()).thenReturn(conn);
        when(conn.createStatement()).thenReturn(stm);
        when(stm.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);

        // Set the mock DataSource using reflection
        try {
            Field dbField = BookingDAO.class.getDeclaredField("db");
            dbField.setAccessible(true);
            dbField.set(bookingDAO, db);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreate_InvalidEmail_ThrowsInputValidationException() {
        BookingData bookingData = new BookingData(
                new PassengerData("John", "Doe", "invalid.email", 1, "1234567890", "USA", "ABCD1234", "1990-01-01"),
                100, "A1", 1);

        assertThrows(InputValidationException.class, () -> {
            bookingDAO.create(bookingData);
        });
    }

    @Test
    public void testCreate_DatabaseError_ThrowsBookingPersistenceException() throws SQLException {
        when(db.getConnection()).thenThrow(new SQLException());

        BookingData bookingData = new BookingData(new PassengerData("John", "Doe", "john.doe@example.com", 1, "1234567890", "USA", "ABCD1234", "1990-01-01"), 100, "A1", 1);

        assertThrows(BookingPersistenceException.class, () -> {
            bookingDAO.create(bookingData);
        });
    }
    
    @Test
    public void testCreate_ConnectionError_ThrowsBookingPersistenceException() throws SQLException {
        when(db.getConnection()).thenThrow(new SQLException());

        BookingData bookingData = new BookingData(new PassengerData("John", "Doe", "john.doe@example.com", 1, "1234567890", "USA", "ABCD1234", "1990-01-01"), 100, "A1", 1);

        assertThrows(BookingPersistenceException.class, () -> {
            bookingDAO.create(bookingData);
        });
    }

}
