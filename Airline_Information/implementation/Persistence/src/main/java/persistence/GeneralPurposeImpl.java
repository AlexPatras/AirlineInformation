//package persistence;
//
//import java.sql.*;
//import java.util.*;
//
//import javax.naming.spi.DirStateFactory.Result;
//import javax.sql.DataSource;
//
//import dataRecords.BookingData;
//import dataRecords.ExtrasData;
//import dataRecords.PassengerData;
//
//public class GeneralPurposeImpl implements GeneralPurpose {
//    private DataSource db = DBController.getDataSource("db");
//
////    /**
////     * Generates certain amount of question marks to put in SQL query
////     * in case where list needs to be passed.
////     *
////     * @param howMany determines how many question marks will be generated.
////     * @return String of question marks, separated by comma
////     */
////    private String generateQuestionMarks(int howMany) {
////        StringBuilder sb = new StringBuilder();
////        for (int i = 0; i < howMany; i++) {
////            sb.append("?");
////            if (i < howMany - 1) {
////                sb.append(", ");
////            }
////        }
////        return sb.toString();
////    }
//
////    /**
////     * Retrieves flight IDs and prices from a flight table where flight destination
////     * is equal to
////     * provided destination. Results are put in a Map.
////     *
////     * @param destination determines which flights will be affected by discount. All
////     *                    flights that have arrival
////     *                    destination as destination provided will be selected
////     * @return Map of integers, which contains flight ids and prices.
////     * @throws DiscountDatabaseException when sql exception is caught
////     */
////    @Override
////    public HashMap<Integer, Integer> getFlightIdsAndPrices(String destination) throws DiscountDatabaseException {
////        try {
////            if (destination == null || destination.equals("")) {
////                throw new DiscountDatabaseException("Internal database error occurred");
////            }
////
////            Connection conn = db.getConnection();
////            String selectUpdatedFlightsSQL = "SELECT flightid, price FROM flight WHERE arrival = ? AND flightid NOT IN ("
////                    +
////                    "SELECT flightid FROM flightdiscount)";
////            PreparedStatement selectUpdatedFlightsPS = conn.prepareStatement(selectUpdatedFlightsSQL);
////            selectUpdatedFlightsPS.setString(1, destination);
////            ResultSet updatedFlightIdsAndPrices = selectUpdatedFlightsPS.executeQuery();
////
////            HashMap<Integer, Integer> flightIdsAndPrices = new HashMap<>();
////
////            while (updatedFlightIdsAndPrices.next()) {
////                flightIdsAndPrices.put(updatedFlightIdsAndPrices.getInt("flightid"),
////                        updatedFlightIdsAndPrices.getInt("price"));
////            }
////
////            updatedFlightIdsAndPrices.close();
////            conn.close();
////
////            if (flightIdsAndPrices.isEmpty()) {
////                throw new DiscountDatabaseException("No flights to this destination without discount found");
////            }
////            return flightIdsAndPrices;
////        } catch (SQLException e) {
////            throw new DiscountDatabaseException(e.getMessage());
////        }
////    }
//
////    /**
////     * Retrieves booking ids and prices from booking table which have a certain
////     * flight (affected by discount) assigned to them in
////     * bookingFlight table.
////     *
////     * @param flightIdsAndPrices is taken to later create a list of flight ids.
////     * @return Map of integers, which has booking id as the key, and its extras price as value (price might be 0)
////     * @throws DiscountDatabaseException when sql exception is caught
////     */
////    @Override
////    public HashMap<Integer, Integer> getBookingIdsAndPrices(HashMap<Integer, Integer> flightIdsAndPrices)
////            throws DiscountDatabaseException {
////        try {
////            Connection conn = db.getConnection();
////
////            List<Integer> flightIds = new ArrayList<>();
////
////            for (Map.Entry<Integer, Integer> e : flightIdsAndPrices.entrySet()) {
////                flightIds.add(e.getKey());
////            }
////
////            String selectBookingExtrasPriceSQL = "SELECT bookingid, price FROM booking WHERE bookingid IN (" +
////                    "SELECT bookingid FROM bookingFlight WHERE flightid IN (" + generateQuestionMarks(flightIds.size())
////                    + "))";
////            PreparedStatement selectBookingExtrasPricePS = conn.prepareStatement(selectBookingExtrasPriceSQL);
////            int parameterIndex = 1;
////            for (Integer id : flightIds) {
////                selectBookingExtrasPricePS.setInt(parameterIndex, id);
////                parameterIndex++;
////            }
////            ResultSet updatedBookingIdsAndPrices = selectBookingExtrasPricePS.executeQuery();
////
////            HashMap<Integer, Integer> bookingIdsAndPrices = new HashMap<>();
////
////            while (updatedBookingIdsAndPrices.next()) {
////                bookingIdsAndPrices.put(updatedBookingIdsAndPrices.getInt("bookingid"),
////                        updatedBookingIdsAndPrices.getInt("price"));
////            }
////
////            updatedBookingIdsAndPrices.close();
////            conn.close();
////            return bookingIdsAndPrices;
////        } catch (SQLException e) {
////            throw new DiscountDatabaseException(e.getMessage());
////        }
////    }
//
////    /**
////     * Retrieves booking and flight ids from "booingFlight" table (based on flight
////     * ids present in passed map - only those bookings that have
////     * flight id assigned will be selected) and stores results in a map
////     * @param flightIdsAndPrices determines which booking and flight ids will be selected
////     * @return map of integers which contains booking id as the key and flight id as value
////     * @throws DiscountDatabaseException when sql exception is caught
////     */
////    @Override
////    public HashMap<Integer, Integer> getBookingAndFlightIds(HashMap<Integer, Integer> flightIdsAndPrices)
////            throws DiscountDatabaseException {
////        try {
////            Connection conn = db.getConnection();
////
////            List<Integer> flightIds = new ArrayList<>();
////            for (Map.Entry<Integer, Integer> e : flightIdsAndPrices.entrySet()) {
////                flightIds.add(e.getKey());
////            }
////
////            String selectBookingAndFlightIdsSQL = "SELECT bookingid, flightid FROM bookingFlight WHERE flightid IN ("
////                    + generateQuestionMarks(flightIds.size()) + ")";
////            PreparedStatement selectBookingAndFlightIdsPS = conn.prepareStatement(selectBookingAndFlightIdsSQL);
////            int parameterIndex = 1;
////            for (Integer id : flightIds) {
////                selectBookingAndFlightIdsPS.setInt(parameterIndex, id);
////                parameterIndex++;
////            }
////            ResultSet selectBookingAndFlightIdsRS = selectBookingAndFlightIdsPS.executeQuery();
////
////            HashMap<Integer, Integer> bookingAndFlightIds = new HashMap<>();
////
////            while (selectBookingAndFlightIdsRS.next()) {
////                bookingAndFlightIds.put(selectBookingAndFlightIdsRS.getInt("bookingid"),
////                        selectBookingAndFlightIdsRS.getInt("flightid"));
////            }
////
////            selectBookingAndFlightIdsRS.close();
////            conn.close();
////            return bookingAndFlightIds;
////        } catch (SQLException e) {
////            throw new DiscountDatabaseException(e.getMessage());
////        }
////    }
//
////    /**
////     * Finds which flights are assigned to certain bookings, combines flight price
////     * and booking extras price
////     * (if it has extras), sets the new price after discount for each concerned
////     * booking and finally updates "booking" table
////     * in database with updated prices
////     * @param bookingAndFlightIds is a map of integers to help assign a flight to a certain booking
////     * @param flightIdsAndPrices is a map of integers that holds flight ids and prices of flights which are being
////     * discounted
////     * @param bookingIdsAndPrices is a map of integers that holds booking ids and price of extras from "booking" table
////     * @param discountPercentage is a double which tells how big the discount is
////     * @throws DiscountDatabaseException when sql exception is caught
////     */
////    @Override
////    public void updateBookingPrices(HashMap<Integer, Integer> bookingAndFlightIds,
////            HashMap<Integer, Integer> flightIdsAndPrices,
////            HashMap<Integer, Integer> bookingIdsAndPrices,
////            double discountPercentage) throws DiscountDatabaseException {
////
////        try {
////            Connection conn = db.getConnection();
////            HashMap<Integer, Integer> bookingIdsAndFullPrices = new HashMap<>();
////
////            for (Map.Entry<Integer, Integer> mainEntry : bookingAndFlightIds.entrySet()) {
////                int idToSet = -1;
////                int finalPrice = -1;
////                int flightPrice = -1;
////                int bookingExtrasPrice = -1;
////                for (Map.Entry<Integer, Integer> bookingEntry : bookingIdsAndPrices.entrySet()) {
////                    if (Objects.equals(mainEntry.getKey(), bookingEntry.getKey())) {
////                        idToSet = bookingEntry.getKey();
////                        bookingExtrasPrice = bookingEntry.getValue();
////                    }
////                }
////                for (Map.Entry<Integer, Integer> flightEntry : flightIdsAndPrices.entrySet()) {
////                    if (Objects.equals(mainEntry.getValue(), flightEntry.getKey())) {
////                        flightPrice = flightEntry.getValue();
////                    }
////                }
////                finalPrice = (int) ((flightPrice + bookingExtrasPrice) * (1 - discountPercentage));
////                bookingIdsAndFullPrices.put(idToSet, finalPrice);
////            }
////
////            String updateBookingPriceSQL = "UPDATE booking SET price = ? WHERE bookingid = ?";
////            PreparedStatement updateBookingPricePS = conn.prepareStatement(updateBookingPriceSQL);
////
////            for (Map.Entry<Integer, Integer> entry : bookingIdsAndFullPrices.entrySet()) {
////                int bookingId = entry.getKey();
////                int bookingPrice = entry.getValue();
////
////                updateBookingPricePS.setInt(1, bookingPrice);
////                updateBookingPricePS.setInt(2, bookingId);
////
////                updateBookingPricePS.executeUpdate();
////            }
////
////            updateBookingPricePS.close();
////            conn.close();
////        } catch (SQLException e) {
////            throw new DiscountDatabaseException(e.getMessage());
////        }
////    }
//
////    /**
////     * Updates "flightdiscount" table: inserts flight ids and dynamic discount value (-1).
////     * @param flightIdsAndPrices is a map of integers that represents flight ids and prices for
////     * those flights which have a dynamic discount applied.
////     * @throws SQLException when sql exception is caught
////     */
////    @Override
////    public void updateCurrentDiscounts(HashMap<Integer, Integer> flightIdsAndPrices) throws DiscountDatabaseException {
////        try {
////            Connection conn = db.getConnection();
////            List<Integer> flightIds = new ArrayList<>();
////
////            for (Map.Entry<Integer, Integer> e : flightIdsAndPrices.entrySet()) {
////                flightIds.add(e.getKey());
////            }
////
////            String updateDiscountTableSQL = "INSERT INTO flightdiscount (flightid, discount) VALUES (?, ?)";
////            PreparedStatement updateDiscountTablePS = conn.prepareStatement(updateDiscountTableSQL);
////
////            for (int id : flightIds) {
////                updateDiscountTablePS.setInt(1, id);
////                updateDiscountTablePS.setInt(2, -1);
////                updateDiscountTablePS.addBatch();
////            }
////
////            updateDiscountTablePS.executeBatch();
////
////            updateDiscountTablePS.close();
////            conn.close();
////        } catch (SQLException e) {
////            throw new DiscountDatabaseException(e.getMessage());
////        }
////
////    }
//
////    /**
////     * Checks whether provided flight id is not present in "flightDiscount" table in database
////     * (thus is applicable for a discount). if it is not present, it returns a result.
////     * @param flightId says which flight should be checked if present.
////     * @return a map of integer and list of strings which contains flight id, its arrival destination and price
////     * @throws DiscountDatabaseException if sql exception is caught
////     */
////    @Override
////    public Map<Integer, List<String>> checkFlightForDiscount(int flightId) throws DiscountDatabaseException {
////        try {
////            Connection conn = db.getConnection();
////            HashMap<Integer, List<String>> flightInfo = new HashMap<>();
////            List<String> flightLocationAndPrice = new ArrayList<>();
////            String sql = "SELECT flightid, arrival, price FROM flight WHERE flightid = ? AND flightid NOT IN (" +
////                    "SELECT flightid FROM flightdiscount)";
////            PreparedStatement ps = conn.prepareStatement(sql);
////            ps.setInt(1, flightId);
////
////            ResultSet rs = ps.executeQuery();
////
////            while (rs.next()) {
////                int id = rs.getInt("flightid");
////                flightLocationAndPrice.add(rs.getString("arrival"));
////                flightLocationAndPrice.add(String.valueOf(rs.getInt("price")));
////                flightInfo.put(id, flightLocationAndPrice);
////            }
////            if (flightInfo.isEmpty()) {
////                throw new SQLException("Flight already has a discount applied");
////            } else if (flightInfo.size() > 1) {
////                throw new SQLException("Error: duplicated flight ids exist in database");
////            }
////            ps.close();
////            conn.close();
////            return flightInfo;
////        } catch (SQLException e) {
////            throw new DiscountDatabaseException(e.getMessage());
////        }
////    }
//
////    /**
////     * Selects distinct list of all flight arrival locations from database
////     * @return list of strings containing names of locations
////     * @throws DiscountDatabaseException when sql exception is caught
////     */
////    @Override
////    public List<String> getFlightDestinations() throws DiscountDatabaseException {
////        try {
////            Connection conn = db.getConnection();
////            List<String> results = new ArrayList<>();
////            String sql = "SELECT DISTINCT arrival FROM flight";
////            Statement stm = conn.createStatement();
////            ResultSet rs = stm.executeQuery(sql);
////            while (rs.next()) {
////                results.add(rs.getString("arrival"));
////            }
////            stm.close();
////            conn.close();
////            return results;
////        } catch (SQLException e) {
////            throw new DiscountDatabaseException(e.getMessage());
////        }
////    }
//
//}
