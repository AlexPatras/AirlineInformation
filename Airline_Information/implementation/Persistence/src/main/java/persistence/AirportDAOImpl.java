package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import dataRecords.AirportData;

public  class AirportDAOImpl implements AirportDAO {

    private DataSource db = DBController.getDataSource("db");

    public AirportDAOImpl() {
    }

    // Constructor for testing purposes
    public AirportDAOImpl(DataSource db) {
        this.db = db;
    }
    
    @Override
    public void storeAirport(AirportData airportData, DataSource ds) throws SQLException {
    
        Connection conn = ds.getConnection();

        try {
            PreparedStatement stm = conn.prepareStatement("INSERT INTO airport (iata, city, country) VALUES (?, ?, ?)");
            stm.setString(1, airportData.iata());
            stm.setString(2, airportData.city());
            stm.setString(3, airportData.country());
            stm.executeUpdate();
            stm.close();
        } finally {
            conn.close();
        }
    }

    @Override
    public List<AirportData> getAllAirports() throws SQLException {
        List<AirportData> airports = new ArrayList<>();
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM airport");
        ResultSet result = statement.executeQuery();

        // While there is a next row, add the airport to the list
        if (result == null) {
            return airports;
        }

        while (result.next()) {
            String iata = result.getString("iata");
            String city = result.getString("city");
            String country = result.getString("country");

            AirportData airport = new AirportData(iata, city, country);
            airports.add(airport);
        }
        return airports;
    }

    @Override
    public boolean airportExists(String iata) throws SQLException {
        List<AirportData> airports = getAllAirports();
    
        for (AirportData airport : airports) {
            if (airport.iata().equals(iata)) {
                return true;
            }
        }

        return false;
    }
}
