package persistence;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import dataRecords.BookingData;
import dataRecords.PassengerData;

public class BookingDAO implements DAO<BookingData> {

    private DataSource db = DBController.getDataSource("db");

    private int generatedBookingId = -1;

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    
    @Override
    public void create(BookingData entity) throws BookingPersistenceException, InputValidationException {

        Connection conn;

        if (entity.passengerData().email().isEmpty()) {
            throw new InputValidationException("P: The email is required.");
        }
        if (!isValidEmail(entity.passengerData().email())) {
            throw new InputValidationException("P: The email must be valid.");
        }

        try {
            conn = db.getConnection();
            String sql = "SELECT COUNT(*) as flightcount FROM flight WHERE flightid = ? ";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, entity.flightID());
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                int flightCount = rs.getInt("flightcount");
                if (flightCount == 0) {
                    throw new IllegalArgumentException("P: The flight ID entered is not valid.");
                }
            }

            stm.close();
            conn.close();
        } catch (SQLException e) {
            throw new BookingPersistenceException("P: Couldn't get flight IDs from the DB.", e);
        }

        ResultSet resultSet = null;

        try {
            conn = db.getConnection();
            String createBookingSQL = "INSERT INTO booking (passengerid, price, seat) VALUES(?, ?, ?) ";
            PreparedStatement createBookingStm = conn.prepareStatement(createBookingSQL,
                    Statement.RETURN_GENERATED_KEYS);
            createBookingStm.setInt(1, entity.passengerData().passengerId());
            createBookingStm.setInt(2, entity.price());
            createBookingStm.setString(3, entity.seat());

            createBookingStm.executeUpdate();
            resultSet = createBookingStm.getGeneratedKeys();

            if (resultSet.next()) {
                generatedBookingId = resultSet.getInt(1);
            }
            createBookingStm.close();
            conn.close();
        } catch (SQLException e) {
            throw new BookingPersistenceException("P: Failed to create booking in DB.", e);
        }

        int isExisting = 0;

        try {
            conn = db.getConnection();
            String checkForExistingBooking = "SELECT COUNT(*) as existing FROM bookingflight WHERE bookingid = ? AND flightid = ?";
            PreparedStatement checkForExistingBookingStm = conn.prepareStatement(checkForExistingBooking);
            checkForExistingBookingStm.setInt(1, generatedBookingId);
            checkForExistingBookingStm.setInt(2, entity.flightID());
            ResultSet existing = checkForExistingBookingStm.executeQuery();

            if (existing.next()) {
                isExisting = existing.getInt("existing");
            }
            checkForExistingBookingStm.close();
            conn.close();
        } catch (SQLException e) {
            throw new BookingPersistenceException("P: Couldn't get existing bookings from DB.", e);
        }

        if (isExisting == 0) {
            try {
                conn = db.getConnection();
                String createBookingFlightSql = "INSERT INTO bookingflight VALUES(?, ?);";
                PreparedStatement createBookingFlightStm = conn.prepareStatement(createBookingFlightSql);
                createBookingFlightStm.setInt(1, generatedBookingId);
                createBookingFlightStm.setInt(2, entity.flightID());
                createBookingFlightStm.executeUpdate();
                createBookingFlightStm.close();
                conn.close();
            } catch (SQLException e) {
                throw new BookingPersistenceException("P: Couldn't associate the booking with a flight.", e);
            }
        } else {
            throw new BookingPersistenceException("P: There already is a booking created for the information provided.");
        }
    }

    @Override
    public BookingData read(int id) throws BookingPersistenceException {

        DAO<PassengerData> passengerDao = new PassengerDAO();
        
        int passengerId = 0;
        int bookingId = 0;
        int price = 0;
        String seat = "";

        try {
            Connection conn = db.getConnection();
            String sql = "SELECT * FROM booking WHERE bookingid = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                passengerId = rs.getInt("passengerid");
                bookingId = id;
                price = rs.getInt("price");
                seat = rs.getString("seat");
            }

            PassengerData passengerData = new PassengerData(null, null, null, 0, null, null, null, null);
            
            try {
                passengerData = passengerDao.read(passengerId);
            } catch (PassengerPersistenceException e) {
                throw new BookingPersistenceException("Couldn't get passenger information.", e);
            } catch (Exception e) {
                throw new BookingPersistenceException("Couldn't get passenger information.", e);
            }

            BookingData bookingData = new BookingData(passengerData, price, seat, id);

            return bookingData;
        } catch (SQLException | BookingPersistenceException e) {
            throw new BookingPersistenceException("Couldn't get booking information from the DB.", e);
        }
    }

    @Override
    public void update(BookingData entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
