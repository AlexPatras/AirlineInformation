package persistence;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import dataRecords.PlaneData;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaneNonDAOImpl implements PlaneNonDAO {

    private DataSource db = DBController.getDataSource("db");

    @Override
    public void storePlane(PlaneData planeData, DataSource ds) throws PlanePersistenceException {

        if (planeData.model().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be empty");
        } else if (planeData.numberOfSeats() < 1) {
            throw new IllegalArgumentException("Number of seats cannot be less than 1");
        }

        try {
            Connection conn = ds.getConnection();
            Statement stm = conn.createStatement();
            String sql = "INSERT INTO plane (model, numberOfSeats) VALUES ('" + planeData.model() + "', "
                    + planeData.numberOfSeats() + ")";
            stm.executeUpdate(sql);

            stm.close();
            conn.close();
        } catch (SQLException e) {
            throw new PlanePersistenceException("Couldn't add plane to DB.", e);
        }
    }

    @Override
    public ArrayList<PlaneData> getAllPlanes(DataSource ds) throws PlanePersistenceException {
        ArrayList<PlaneData> planes = new ArrayList<>();
        String sql = "SELECT * FROM plane";

        try (Connection conn = ds.getConnection(); Statement stm = conn.createStatement();) {

            ResultSet result = stm.executeQuery(sql);

            while (result.next()) {
                int ID = result.getInt("id");
                String model = result.getString("model");
                int numberOfSeats = result.getInt("numberofseats");

                PlaneData plane = new PlaneData(ID, model, numberOfSeats);
                planes.add(plane);
            }
        } catch (SQLException e) {
            Logger.getLogger(PlaneNonDAOImpl.class.getName()).log(Level.SEVERE, sql);
        }
        return planes;
    }

    @Override
    public int getNumberOfSeats(int flightid) throws PlanePersistenceException {
        int planeid = -1;
        int nrOfSeats = -2;

        try {
            Connection conn = db.getConnection();
            String sql = "SELECT planeid FROM flightplane WHERE flightid = " + flightid + ";";
            Statement getPlaneIdStm = conn.createStatement();
            ResultSet planeIdRs = getPlaneIdStm.executeQuery(sql);

            if (planeIdRs.next()) {
                planeid = planeIdRs.getInt(1);
            }

            getPlaneIdStm.close();

            sql = "SELECT numberofseats FROM plane WHERE id = " + planeid + ";";
            Statement getNrOfSeatsStm = conn.createStatement();
            ResultSet nrOfSeatsRs = getNrOfSeatsStm.executeQuery(sql);

            if (nrOfSeatsRs.next()) {
                nrOfSeats = nrOfSeatsRs.getInt(1);
            }

            return nrOfSeats;
        } catch (SQLException e) {
            throw new PlanePersistenceException("P: Couldn't get number of seats from the DB.", e);
        }

    }

    @Override
    public ArrayList<String> getOccupiedSeats() throws PlanePersistenceException {
        ArrayList<String> occupiedSeats = new ArrayList<>();
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT seat FROM booking";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            while (rs.next()) {
                String occupiedSeat = rs.getString("seat");
                occupiedSeats.add(occupiedSeat);
            }
        } catch (SQLException e) {
            throw new PlanePersistenceException("P: Couldn't get occupied seats from the DB.", e);
        }

        return occupiedSeats;
    }

}
