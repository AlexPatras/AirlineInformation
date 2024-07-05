package persistence;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightDiscountNonDAOImpl implements FlightDiscountNonDAO{

    private DataSource db = DBController.getDataSource("db");


    /**
     * Updates "flightdiscount" table: inserts flight ids and dynamic discount value (-1).
     * @param flightIdsAndPrices is a map of integers that represents flight ids and prices for
     * those flights which have a dynamic discount applied.
     * @throws SQLException when sql exception is caught
     */
    @Override
    public void updateCurrentDiscounts(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();
            List<Integer> flightIds = new ArrayList<>();

            for (Map.Entry<Integer, Integer> e : flightIdsAndPrices.entrySet()) {
                flightIds.add(e.getKey());
            }

            String updateDiscountTableSQL = "INSERT INTO flightdiscount (flightid, discount) VALUES (?, ?)";
            PreparedStatement updateDiscountTablePS = conn.prepareStatement(updateDiscountTableSQL);

            for (int id : flightIds) {
                updateDiscountTablePS.setInt(1, id);
                updateDiscountTablePS.setInt(2, -1);
                updateDiscountTablePS.addBatch();
            }

            updateDiscountTablePS.executeBatch();

            updateDiscountTablePS.close();
            conn.close();
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }

    }

}
