package persistence;

import java.sql.*;
import java.util.*;

import javax.sql.DataSource;

public class BookingNonDAOImpl implements BookingNonDAO {

    private DataSource db = DBController.getDataSource("db");

    private int nextFreeBookingId;
    
    @Override
    public int getInsertedBookingId() throws BookingPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT MAX(bookingid) from booking";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);

            if (rs.next()) {
                this.nextFreeBookingId = rs.getInt(1);
            }

            stm.close();
            conn.close();

            return this.nextFreeBookingId;
        } catch (SQLException e) {
            throw new BookingPersistenceException("P: Couldn't get inserted booking id from DB.", e);
        }
    }


    /**
     * Retrieves booking ids and prices from booking table which have a certain
     * flight (which could be affected by discount) assigned to them in
     * bookingFlight table.
     *
     * @param flightIdsAndPrices is taken to later create a list of flight ids.
     * @return Map of integers, which has booking id as the key, and its extras price as value (price might be 0)
     * @throws DiscountDatabaseException when sql exception is caught
     */
    @Override
    public HashMap<Integer, Integer> getBookingIdsAndPrices(HashMap<Integer, Integer> flightIdsAndPrices)
            throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();

            List<Integer> flightIds = new ArrayList<>();

            for (Map.Entry<Integer, Integer> e : flightIdsAndPrices.entrySet()) {
                flightIds.add(e.getKey());
            }

            String selectBookingExtrasPriceSQL = "SELECT bookingid, price FROM booking WHERE bookingid IN (" +
                    "SELECT bookingid FROM bookingFlight WHERE flightid IN (" + generateQuestionMarks(flightIds.size())
                    + "))";
            PreparedStatement selectBookingExtrasPricePS = conn.prepareStatement(selectBookingExtrasPriceSQL);
            int parameterIndex = 1;
            for (Integer id : flightIds) {
                selectBookingExtrasPricePS.setInt(parameterIndex, id);
                parameterIndex++;
            }
            ResultSet updatedBookingIdsAndPrices = selectBookingExtrasPricePS.executeQuery();

            HashMap<Integer, Integer> bookingIdsAndPrices = new HashMap<>();

            while (updatedBookingIdsAndPrices.next()) {
                bookingIdsAndPrices.put(updatedBookingIdsAndPrices.getInt("bookingid"),
                        updatedBookingIdsAndPrices.getInt("price"));
            }

            updatedBookingIdsAndPrices.close();
            conn.close();
            return bookingIdsAndPrices;
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }
    }


    /**
     * Finds which flights are assigned to certain bookings, combines flight price
     * and booking extras price
     * (if it has extras), sets the new price after discount for each concerned
     * booking and finally updates "booking" table
     * in database with updated prices
     * @param bookingAndFlightIds is a map of integers to help assign a flight to a certain booking
     * @param flightIdsAndPrices is a map of integers that holds flight ids and prices of flights which are being
     * discounted
     * @param bookingIdsAndPrices is a map of integers that holds booking ids and price of extras from "booking" table
     * @param discountPercentage is a double which tells how big the discount is
     * @throws DiscountDatabaseException when sql exception is caught
     */
    @Override
    public void updateBookingPrices(HashMap<Integer, Integer> bookingAndFlightIds,
                                    HashMap<Integer, Integer> flightIdsAndPrices,
                                    HashMap<Integer, Integer> bookingIdsAndPrices,
                                    double discountPercentage) throws DiscountDatabaseException {

        try {
            Connection conn = db.getConnection();
            HashMap<Integer, Integer> bookingIdsAndFullPrices = new HashMap<>();

            for (Map.Entry<Integer, Integer> mainEntry : bookingAndFlightIds.entrySet()) {
                int idToSet = -1;
                int finalPrice = -1;
                int flightPrice = -1;
                int bookingExtrasPrice = -1;
                for (Map.Entry<Integer, Integer> bookingEntry : bookingIdsAndPrices.entrySet()) {
                    if (Objects.equals(mainEntry.getKey(), bookingEntry.getKey())) {
                        idToSet = bookingEntry.getKey();
                        bookingExtrasPrice = bookingEntry.getValue();
                    }
                }
                for (Map.Entry<Integer, Integer> flightEntry : flightIdsAndPrices.entrySet()) {
                    if (Objects.equals(mainEntry.getValue(), flightEntry.getKey())) {
                        flightPrice = flightEntry.getValue();
                    }
                }
                if (bookingExtrasPrice > flightPrice) {
                    bookingExtrasPrice = bookingExtrasPrice - flightPrice;
                }
                if (flightPrice == bookingExtrasPrice) {
                    finalPrice = (int) ((flightPrice) * (1 - discountPercentage));
                } else {
                    finalPrice = (int) ((flightPrice + bookingExtrasPrice) * (1 - discountPercentage));
                }
                bookingIdsAndFullPrices.put(idToSet, finalPrice);
            }

            String updateBookingPriceSQL = "UPDATE booking SET price = ? WHERE bookingid = ?";
            PreparedStatement updateBookingPricePS = conn.prepareStatement(updateBookingPriceSQL);

            for (Map.Entry<Integer, Integer> entry : bookingIdsAndFullPrices.entrySet()) {
                int bookingId = entry.getKey();
                int bookingPrice = entry.getValue();

                updateBookingPricePS.setInt(1, bookingPrice);
                updateBookingPricePS.setInt(2, bookingId);

                updateBookingPricePS.executeUpdate();
            }

            updateBookingPricePS.close();
            conn.close();
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }
    }

    /**
     * updates single booking price
     * @param bookingId determines which booking price will be updated
     * @param newPrice the value of new price
     */
    public void calculateSingleBookingPrice(int bookingId, int newPrice) throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();
            String sql = "UPDATE booking SET price = ? WHERE bookingid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, newPrice);
            ps.setInt(2, bookingId);
            ps.executeUpdate();

            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }
    }






    /**
     * Generates certain amount of question marks to put in SQL query
     * in case where list needs to be passed.
     *
     * @param howMany determines how many question marks will be generated.
     * @return String of question marks, separated by comma
     */
    private String generateQuestionMarks(int howMany) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < howMany; i++) {
            sb.append("?");
            if (i < howMany - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    @Override
    public int getDefaultBookingPrice(int bookingId) throws BookingPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT price FROM booking WHERE bookingid = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, bookingId);

            ResultSet rs = stm.executeQuery();

            int price = 0;

            while (rs.next()) {
                price = rs.getInt("price");
            }

            return price;
        } catch (SQLException e) {
            throw new BookingPersistenceException("Couldn't get default booking price", e);
        }
    }
}
