package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import dataRecords.ExtrasData;

public class ExtrasNonDAOImpl implements ExtrasNonDAO {

    private DataSource db = DBController.getDataSource("db");

    @Override
    public ArrayList<Integer> getExtrasIds(int flightId) throws ExtrasPersistenceException {
        try {
            Connection conn = db.getConnection();
            String getPlaneSql = "SELECT planeid FROM flightplane WHERE flightid = ?";
            PreparedStatement getPlaneStm = conn.prepareStatement(getPlaneSql);
            getPlaneStm.setInt(1, flightId);

            ResultSet planeIdResultSet = getPlaneStm.executeQuery();
            int planeId = 0;

            while (planeIdResultSet.next()) {
                planeId = planeIdResultSet.getInt("planeid");
            }
            getPlaneStm.close();

            ArrayList<Integer> extrasIds = new ArrayList<>();

            String getExtrasSql = "SELECT extrasid FROM hasextras WHERE planeid = ?";
            PreparedStatement getExtrasStm = conn.prepareStatement(getExtrasSql);
            getExtrasStm.setInt(1, planeId);

            ResultSet extrasIdsResultSet = getExtrasStm.executeQuery();

            while (extrasIdsResultSet.next()) {
                extrasIds.add(extrasIdsResultSet.getInt("extrasid"));
            }

            getExtrasStm.close();

            return extrasIds;

        } catch (SQLException e) {
            throw new ExtrasPersistenceException("P: Couldn't retrieve extras ids from DB.", e);
        }

    }

    @Override
    public ExtrasData getExtras(int extrasId) throws ExtrasPersistenceException {
        try {
            Connection conn = db.getConnection();
            String getDescSql = "SELECT description, price FROM extras WHERE extrasid = ?";
            PreparedStatement pstm = conn.prepareStatement(getDescSql);
            pstm.setInt(1, extrasId);

            ResultSet rs = pstm.executeQuery();

            String extrasDescription = "";
            int extrasPrice = -1;

            while (rs.next()) {
                extrasDescription = rs.getString("description");
                extrasPrice = rs.getInt("price");
            }

            pstm.close();
            conn.close();

            ExtrasData extrasData = new ExtrasData(extrasId, extrasDescription, extrasPrice);

            return extrasData;

        } catch (SQLException e) {
            throw new ExtrasPersistenceException("P: Couldn't retrieve extras from DB.", e);
        }
    }

    @Override
    public void createBookingExtras(int extrasId, int bookingId) throws ExtrasPersistenceException {
        try {
            Connection conn = db.getConnection();
            String insertSql = "INSERT INTO bookingextras VALUES(?, ?)";
            PreparedStatement pstm = conn.prepareStatement(insertSql);

            pstm.setInt(1, extrasId);
            pstm.setInt(2, bookingId);

            pstm.executeUpdate();

            pstm.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new ExtrasPersistenceException("P: Couldn't add extras to booking.", e);
        }
    }

    @Override
    public int getExtrasPrice(int extrasId) throws ExtrasPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT price FROM extras WHERE extrasid = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, extrasId);

            ResultSet rs = stm.executeQuery();

            int price = 0;

            while (rs.next()) {
                price = rs.getInt("price");
            }

            stm.close();
            conn.close();

            return price;
        } catch (SQLException e) {
            throw new ExtrasPersistenceException("P: Couldn't gen extras price from DB.", e);
        }
    }
}
