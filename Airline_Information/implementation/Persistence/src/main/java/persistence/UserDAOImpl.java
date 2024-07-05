package persistence;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import dataRecords.PlaneData;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAOImpl implements UserDAO {

    private DataSource db = DBController.getDataSource("db");

    @Override
    public void storePlane(PlaneData planeData, DataSource ds) throws SQLException {

        if (planeData.model().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be empty");
        } else if (planeData.numberOfSeats() < 1) {
            throw new IllegalArgumentException("Number of seats cannot be less than 1");
        }

        Connection conn = ds.getConnection();
        Statement stm = conn.createStatement();
        String sql = "INSERT INTO plane (model, numberOfSeats) VALUES ('" + planeData.model() + "', "
                + planeData.numberOfSeats() + ")";
        stm.executeUpdate(sql);

        stm.close();
        conn.close();
    }

    @Override
    public ArrayList<PlaneData> getAllPlanes(DataSource ds) {
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
            Logger.getLogger(UserDAOImpl.class.getName()).log(Level.SEVERE, sql);
        }
        return planes;
    }
    
    public int getNumberOfSeats(int flightid) throws SQLException {
        int planeid = -1;
        int nrOfSeats = -2;

        String sql = "SELECT planeid FROM flightplane WHERE flightid = " + flightid + ";" ;
        Connection conn = db.getConnection();
        Statement getPlaneIdStm = conn.createStatement();
        ResultSet planeIdRs = getPlaneIdStm.executeQuery(sql);

        if (planeIdRs.next()) {
            planeid = planeIdRs.getInt(1);
        }

        getPlaneIdStm.close();

        sql = "SELECT numberofseats FROM plane WHERE id = " + planeid + ";" ;
        Statement getNrOfSeatsStm = conn.createStatement();
        ResultSet nrOfSeatsRs = getNrOfSeatsStm.executeQuery(sql);

        if (nrOfSeatsRs.next()) {
            nrOfSeats = nrOfSeatsRs.getInt(1);
        }

        return nrOfSeats;

    }

}
