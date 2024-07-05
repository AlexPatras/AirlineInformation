package persistence;

import dataRecords.StartSalesData;
import dataRecords.PlaneData;
import persistence.StartSalesDAO;
import persistence.StartSalesDAOImpl;
import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class StartSalesDAOImpl implements StartSalesDAO {

	private static final StartSalesData StartSalesff = null;
	private DataSource db = DBController.getDataSource("db");

	public void create(StartSalesData startSalesData, DataSource ds) throws StartSalesPersistenceException {
		try {
			Connection conn = ds.getConnection();
			String sql = "INSERT INTO startSale (gatenr) VALUES (?)";
			PreparedStatement stm = conn.prepareStatement(sql);

			stm.setInt(9, startSalesData.flightID());

			stm.executeUpdate();
			stm.close();
			conn.close();
		} catch (SQLException e) {
			throw new StartSalesPersistenceException("The Sale couldn't be registered", e);
		}
	}

	public StartSalesData getAllStartSales() throws StartSalesPersistenceException {

		try {
			Connection conn = db.getConnection();
			String sql = "SELECT * FROM startSales";
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				// TODO: change string to Airport when its created (departure and arrival)
				int Flight = rs.getInt("FlightID");

			}
			//

			rs.close();
			ps.close();
			conn.close();

			return StartSalesff;
		} catch (SQLException e) {
			throw new StartSalesPersistenceException("P: Couldn't get all flights.", e);
		}

	}

	public StartSalesData getStartSales(int flightID) throws StartSalesPersistenceException {

		try {
			Connection conn = db.getConnection();
			String sql = "SELECT flightid FROM flight WHERE departure = ? and arrival = ? and depdate = ?";
			PreparedStatement stm = conn.prepareStatement(sql);

			stm.setInt(1, flightID);

			ResultSet rs = stm.executeQuery();

			while (rs.next()) {

				StartSalesData startSalesData = new StartSalesData(flightID, sql);

				ArrayList flightsFound;

				String Sales = rs.getString("flightid");

			}

			if (Sales.isEmpty()) {
				System.out.println("Empty");
			}

			StartSalesData startSalesData = new StartSalesData(flightID, sql);

			ArrayList startSalesFound;

		} catch (SQLException e) {
			throw new StartSalesPersistenceException("P: Couldn't retrieve flight and plane ids from DB.", e);
		}

		return getAllStartSales();
	}

}
