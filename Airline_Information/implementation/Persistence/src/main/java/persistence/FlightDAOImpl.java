package persistence;

import dataRecords.FlightData;
import dataRecords.PlaneData;

import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class FlightDAOImpl implements FlightDAO {

    private DataSource db = DBController.getDataSource("db");
    
   @Override
    public void create(FlightData entity) throws FlightPersistenceException {
        Connection conn;
        try {
            conn = db.getConnection();
            String sql = "INSERT INTO flight (departure, arrival, deptime, arrtime, depdate, arrdate, price,currency, gatenr, planemodel, numberofseats) VALUES (?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatment = conn.prepareStatement(sql);
            preparedStatment.setString(1, entity.departureAirport());
            preparedStatment.setString(2, entity.arrivalAirport());
            preparedStatment.setTime(3, Time.valueOf(entity.departureTime()));
            preparedStatment.setTime(4, Time.valueOf(entity.arrivalTime()));
            preparedStatment.setDate(5, Date.valueOf(entity.departureDate()));
            preparedStatment.setDate(6, Date.valueOf(entity.arrivalDate()));
            preparedStatment.setInt(7, entity.price());
            preparedStatment.setString(8, entity.currency());
            preparedStatment.setString(9, entity.gateNr());
            preparedStatment.setString(10, entity.planeData().model());
            preparedStatment.setInt(11, entity.planeData().numberOfSeats());

            preparedStatment.executeUpdate();
            preparedStatment.close();
            conn.close();
        } catch (SQLException e) {
            throw new FlightPersistenceException("The flight couldn't be registered", e);
        }
    }
}
