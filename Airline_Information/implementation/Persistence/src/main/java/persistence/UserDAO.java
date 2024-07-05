package persistence;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import dataRecords.AirportData;
import dataRecords.PlaneData;

public interface UserDAO {
    public void storePlane(PlaneData planeData, DataSource ds) throws SQLException;
    public ArrayList<PlaneData> getAllPlanes(DataSource ds);
    public int getNumberOfSeats(int id) throws SQLException;
}
