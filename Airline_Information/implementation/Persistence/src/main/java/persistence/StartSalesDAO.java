package persistence;

import java.sql.Statement;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.naming.spi.DirStateFactory.Result;
import javax.sql.DataSource;

import dataRecords.StartSalesData;

public class StartSalesDAO implements DAO<StartSalesData> {

	private DataSource db = DBController.getDataSource("db");

	int generatedStartSalesId = -1;

	@Override
	public void create(StartSalesData entity) throws StartSalesPersistenceException, InputValidationException {

		Connection conn;

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
			throw new StartSalesPersistenceException("P: Couldn't get flight IDs from the DB.", e);
		}

		int isExisting = 0;

		try {
			conn = db.getConnection();
			String checkForExistingStartSales = "SELECT COUNT(*) as existing FROM bookingflight WHERE bookingid = ? AND flightid = ?";
			PreparedStatement checkForExistingStartSalesStm = conn.prepareStatement(checkForExistingStartSales);
			checkForExistingStartSalesStm.setInt(1, generatedStartSalesId);
			checkForExistingStartSalesStm.setInt(2, entity.flightID());
			ResultSet existing = checkForExistingStartSalesStm.executeQuery();

			if (existing.next()) {
				isExisting = existing.getInt("existing");
			}
			checkForExistingStartSalesStm.close();
			conn.close();
		} catch (SQLException e) {
			throw new StartSalesPersistenceException("P: Couldn't get existing bookings from DB.", e);
		}

		if (isExisting == 0) {
			try {
				conn = db.getConnection();
				String createBookingFlightSql = "INSERT INTO bookingflight VALUES(?, ?);";
				PreparedStatement createBookingFlightStm = conn.prepareStatement(createBookingFlightSql);
				createBookingFlightStm.setInt(1, generatedStartSalesId);
				createBookingFlightStm.setInt(2, entity.flightID());
				createBookingFlightStm.executeUpdate();
				createBookingFlightStm.close();
				conn.close();
			} catch (SQLException e) {
				throw new StartSalesPersistenceException("P: Couldn't associate the booking with a flight.", e);
			}
		} else {
			throw new StartSalesPersistenceException(
					"P: There already is a booking created for the information provided.");
		}
	}

	@Override
	public StartSalesData read(int id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'read'");
	}

	@Override
	public void update(StartSalesData entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'update'");
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'delete'");
	}

}
