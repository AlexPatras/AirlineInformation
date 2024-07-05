package businessLogic;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import dataRecords.AirportData;
import persistence.AirportDAO;

public interface AirportManager {
    
    void add(AirportData airportData) throws SQLException;

    public List<AirportData> getAllAirports() throws SQLException;

    public void setAirportDAO(AirportDAO newAirportDao);

    public void setDataSource(DataSource db);
}
