package persistence;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import dataRecords.PlaneData;

public interface PlaneNonDAO {
    public void storePlane(PlaneData planeData, DataSource ds) throws PlanePersistenceException;
    public ArrayList<PlaneData> getAllPlanes(DataSource ds) throws PlanePersistenceException;
    public int getNumberOfSeats(int id) throws PlanePersistenceException;
    ArrayList<String> getOccupiedSeats() throws PlanePersistenceException;
}
