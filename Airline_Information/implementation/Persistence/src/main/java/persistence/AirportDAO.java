package persistence;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import dataRecords.AirportData;

public interface AirportDAO {
    
    void storeAirport(AirportData airportData, DataSource dataSource) throws SQLException;

    public List<AirportData> getAllAirports() throws SQLException;

    public boolean airportExists(String iata) throws SQLException;
}
