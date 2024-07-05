package businessLogic;

import dataRecords.PlaneData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import persistence.DBController;
import persistence.PlaneNonDAO;
import persistence.PlaneNonDAOImpl;
import persistence.PlanePersistenceException;

public class PlaneManager {

    private PlaneNonDAO planeNonDAO = new PlaneNonDAOImpl();
    private DataSource db = DBController.getDataSource("db");

    public void add(PlaneData planeData) throws PlaneBusinessException {
        try {
            planeNonDAO.storePlane(planeData, db);
        } catch (PlanePersistenceException e) {
            throw new PlaneBusinessException("BL: Couldn't add plane.", e);
        }
    }

    public List<PlaneData> getAllPlanes() throws PlaneBusinessException {
        try {
            return planeNonDAO.getAllPlanes(db);
        } catch (PlanePersistenceException e) {
            throw new PlaneBusinessException("BL: Couldn't get all planes.", e);
        }
    }

    public int getNumberOfSeats(int flightID) throws PlanePersistenceException {
        try {
            return planeNonDAO.getNumberOfSeats(flightID);
        } catch (Exception e) {
            throw new PlanePersistenceException("Couldn't get number of seats for the plane.", e);
        }
    }

    public ArrayList<String> getOccupiedSeats() throws PlanePersistenceException {
        try {
            return planeNonDAO.getOccupiedSeats();
        } catch (PlanePersistenceException e) {
            throw new PlanePersistenceException("BL: Couldn't get occupied seats.", e);
        }
    }

    public void setPlaneNonDao(PlaneNonDAO planeNonDAO) {
        this.planeNonDAO = planeNonDAO;
    }
}
