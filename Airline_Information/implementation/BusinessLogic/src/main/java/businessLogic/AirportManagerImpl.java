package businessLogic;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import dataRecords.AirportData;
import persistence.AirportDAO;
import persistence.AirportDAOImpl;
import persistence.DBController;

public class AirportManagerImpl implements AirportManager {
    private AirportDAO airportDAO;
    private DataSource db = DBController.getDataSource("db");

    public AirportManagerImpl() {
        this.airportDAO = new AirportDAOImpl();
    }

    public void add(AirportData airportData) throws SQLException{
        if (airportData.iata().isEmpty()) {
            throw new IllegalArgumentException("IATA cannot be empty");
        } else if (airportData.city().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        } else if (airportData.country().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be empty");
        } else if (airportData.iata().length() != 3) {
            throw new IllegalArgumentException("IATA must be 3 characters long");
        } else if (airportData.iata().matches(".*\\d.*")) {
            throw new IllegalArgumentException("IATA Number");
        } else if (airportData.city().matches(".*\\d.*")) {
            throw new IllegalArgumentException("City Number");
        } else if (airportData.country().matches(".*\\d.*")) {
            throw new IllegalArgumentException("Country Number");
        }

        airportDAO.storeAirport(airportData, db);
    }

    public List<AirportData> getAllAirports() throws SQLException {
        return airportDAO.getAllAirports();
    }

    // for testing purposes
    public void setAirportDAO(AirportDAO newAirportDao) {
        this.airportDAO = newAirportDao;
    }

    // for testing purposes
    public void setDataSource(DataSource db) {
        this.db = db;
    }
}
