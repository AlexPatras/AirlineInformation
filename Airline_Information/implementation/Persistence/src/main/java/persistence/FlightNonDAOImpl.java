package persistence;

import dataRecords.FlightData;
import dataRecords.PlaneData;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public class FlightNonDAOImpl implements FlightNonDAO {

    private DataSource db = DBController.getDataSource("db");

    @Override
    public String getArrivalIATACode(int flightId) throws FlightPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT arrival FROM flight WHERE flightid = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setInt(1, flightId);

            ResultSet rs = stm.executeQuery();

            String code = "";

            while (rs.next()) {
                code = rs.getString("arrival");
            }

            stm.close();
            conn.close();
            return code;

        } catch (SQLException e) {
            throw new FlightPersistenceException("P: Couldn't get arrival IATA code from DB.", e);
        }
    }

    /**
     * Retrieves flight IDs and prices from a flight table where flight
     * destination is equal to provided destination. Results are put in a Map.
     *
     * @param destination determines which flights will be affected by discount.
     *                    All flights that have arrival destination as destination
     *                    provided will be
     *                    selected
     * @return Map of integers, which contains flight ids and prices.
     * @throws DiscountDatabaseException when sql exception is caught
     */
    @Override
    public HashMap<Integer, Integer> getFlightIdsAndPrices(String destination) throws DiscountDatabaseException {
        try {
            if (destination == null || destination.equals("")) {
                throw new DiscountDatabaseException("destination is null or empty");
            }

            Connection conn = db.getConnection();
            String selectUpdatedFlightsSQL = "SELECT flightid, price FROM flight WHERE arrival = ? AND flightid NOT IN ("
                    + "SELECT flightid FROM flightdiscount)";
            PreparedStatement selectUpdatedFlightsPS = conn.prepareStatement(selectUpdatedFlightsSQL);
            selectUpdatedFlightsPS.setString(1, destination);
            ResultSet updatedFlightIdsAndPrices = selectUpdatedFlightsPS.executeQuery();

            HashMap<Integer, Integer> flightIdsAndPrices = new HashMap<>();

            while (updatedFlightIdsAndPrices.next()) {
                flightIdsAndPrices.put(updatedFlightIdsAndPrices.getInt("flightid"),
                        updatedFlightIdsAndPrices.getInt("price"));
            }

            updatedFlightIdsAndPrices.close();
            selectUpdatedFlightsPS.close();
            conn.close();

            if (flightIdsAndPrices.isEmpty()) {
                throw new DiscountDatabaseException("No flights to this destination without discount found");
            }
            return flightIdsAndPrices;
        } catch (SQLException e) {
            throw new DiscountDatabaseException("Problem with getting flight ids and prices: " + e.getMessage());
        }
    }

    /**
     * Checks whether provided flight id is not present in "flightDiscount"
     * table in database (thus is applicable for a discount). if it is not
     * present, it returns a result.
     *
     * @param flightId says which flight should be checked if present.
     * @return a map of integer and list of strings which contains flight id,
     *         its arrival destination and price
     * @throws DiscountDatabaseException if sql exception is caught
     */
    @Override
    public Map<Integer, List<String>> checkFlightForDiscount(int flightId) throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();
            HashMap<Integer, List<String>> flightInfo = new HashMap<>();
            List<String> flightLocationAndPrice = new ArrayList<>();
            String sql = "SELECT flightid, arrival, price FROM flight WHERE flightid = ? AND flightid NOT IN ("
                    + "SELECT flightid FROM flightdiscount)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flightId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("flightid");
                flightLocationAndPrice.add(rs.getString("arrival"));
                flightLocationAndPrice.add(String.valueOf(rs.getInt("price")));
                flightInfo.put(id, flightLocationAndPrice);
            }
            if (flightInfo.isEmpty()) {
                throw new SQLException("Flight already has a discount applied");
            } else if (flightInfo.size() > 1) {
                throw new SQLException("Error: duplicated flight ids exist in database");
            }
            rs.close();
            ps.close();
            conn.close();
            return flightInfo;
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }
    }

    /**
     * Selects distinct list of all flight arrival locations from database
     *
     * @return list of strings containing names of locations
     * @throws DiscountDatabaseException when sql exception is caught
     */
    @Override
    public List<String> getFlightDestinations() throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();
            List<String> results = new ArrayList<>();
            String sql = "SELECT DISTINCT arrival FROM flight";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                results.add(rs.getString("arrival"));
            }
            stm.close();
            conn.close();
            return results;
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }
    }

    /**
     * Checks if flight with a certain id has a discount. If it does, it returns
     * empty HashMap. Otherwise, id, arrival location and price of flight are
     * returned
     *
     * @param flightId determines which flight to look for
     * @return HashMap which either has values or is empty, if search did not
     *         find anything
     */
    @Override
    public HashMap<Integer, List<String>> checkIfFlightHasDiscount(int flightId) throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();
            HashMap<Integer, List<String>> flightInfo = new HashMap<>();
            List<String> flightLocationAndPrice = new ArrayList<>();

            String sql = "SELECT flightId, arrival, price FROM flight WHERE flightid = ? AND flightid IN (" +
                    "SELECT flightid FROM flightdiscount)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, flightId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("flightid");
                flightLocationAndPrice.add(rs.getString("arrival"));
                flightLocationAndPrice.add(String.valueOf(rs.getInt("price")));
                flightInfo.put(id, flightLocationAndPrice);
            }
            rs.close();
            ps.close();
            conn.close();

            return flightInfo;
        } catch (SQLException e) {
            throw new DiscountDatabaseException("Problem with checking if flight has discount: " + e.getMessage());
        }
    }

    /**
     * Select single flight from "flight" table in database.
     *
     * @param flightId determines which flight to look for
     * @return HashMap containing id, arrival location and price of the flight
     */
    @Override
    public HashMap<Integer, List<String>> getSingleFlightInfo(int flightId) throws DiscountDatabaseException {
        try {
            Connection conn = db.getConnection();
            HashMap<Integer, List<String>> flightInfo = new HashMap<>();
            List<String> flightLocationAndPrice = new ArrayList<>();

            String sql = "SELECT flightid, arrival, price FROM flight WHERE flightid = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, flightId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("flightid");
                flightLocationAndPrice.add(rs.getString("arrival"));
                flightLocationAndPrice.add(String.valueOf(rs.getInt("price")));
                flightInfo.put(id, flightLocationAndPrice);
            }
            rs.close();
            ps.close();
            conn.close();
            return flightInfo;
        } catch (SQLException e) {
            throw new DiscountDatabaseException(e.getMessage());
        }
    }

    public boolean CheckRegisterFlights(FlightData flightData, DataSource ds) throws FlightPersistenceException {
        try {
            Connection conn = ds.getConnection();
            String sql = "SELECT COUNT(*) FROM flight WHERE departure = ? AND arrival = ? AND deptime = ? AND arrtime = ? AND depdate = ? AND arrdate = ? AND price = ? AND currency = ? AND gatenr = ? AND planemodel = ? AND numberofseats = ? ";
            PreparedStatement preparedStatment = conn.prepareStatement(sql);
            preparedStatment.setString(1, flightData.departureAirport());
            preparedStatment.setString(2, flightData.arrivalAirport());
            preparedStatment.setTime(3, Time.valueOf(flightData.departureTime()));
            preparedStatment.setTime(4, Time.valueOf(flightData.arrivalTime()));
            preparedStatment.setDate(5, Date.valueOf(flightData.departureDate()));
            preparedStatment.setDate(6, Date.valueOf(flightData.arrivalDate()));
            preparedStatment.setInt(7, flightData.price());
            preparedStatment.setString(8, flightData.currency());
            preparedStatment.setString(9, flightData.gateNr());
            preparedStatment.setString(10, flightData.planeData().model());
            preparedStatment.setInt(11, flightData.planeData().numberOfSeats());

            ResultSet resultSet = preparedStatment.executeQuery();
            resultSet.next();
            int rowCount = resultSet.getInt(1);

            resultSet.close();
            preparedStatment.close();
            conn.close();

            return rowCount > 0;
        } catch (SQLException e) {
            throw new FlightPersistenceException("Error checking flight existence", e);
        }
    }

    @Override
    public ArrayList<String> getIATACode() throws FlightPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT iata FROM airport";
            PreparedStatement preparedStatment = conn.prepareStatement(sql);
            ResultSet rs = preparedStatment.executeQuery();

            ArrayList<String> iataCodes = new ArrayList<>();

            while (rs.next()) {
                String iataCode = rs.getString("iata");
                iataCodes.add(iataCode);

            }
            rs.close();
            preparedStatment.close();
            conn.close();

            return iataCodes;

        } catch (SQLException e) {
            throw new FlightPersistenceException("Couldn't retrieve  IATA codes");
        }
    }
        public ArrayList<String> getPlaneDetails() throws FlightPersistenceException {
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT  model, numberofseats FROM plane";
            PreparedStatement preparedStatment = conn.prepareStatement(sql);
            ResultSet rs = preparedStatment.executeQuery();

            ArrayList<String> modelAndSeats = new ArrayList<>();

            while (rs.next()) {
                String model = rs.getString("model");
                int numberOfSeats = rs.getInt("numberofseats");

                String modelAndSeat = model + " - " + numberOfSeats;
                modelAndSeats.add(modelAndSeat);
            }
            rs.close();
            preparedStatment.close();
            conn.close();

            return modelAndSeats;
        } catch (SQLException e) {
            throw new FlightPersistenceException("Couldn't retrieve model and number of seats", e);
        }
    }
    

    @Override
    public List<FlightData> getAllFlights() throws FlightPersistenceException {
        List<FlightData> flights = new ArrayList<>();

        try {
            Connection conn = db.getConnection();
            String sql = "SELECT * FROM flight";
            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            List<PlaneData> planes = getPlanesForFlights();
            HashMap<Integer, Integer> ids = getFlightAndPlaneIds();

            while (rs.next()) {
                int id = rs.getInt("flightid");
                // TODO: change string to Airport when its created (departure and arrival)
                String depAirport = rs.getString("departure");
                String arrAirport = rs.getString("arrival");
                Time depTime = rs.getTime("deptime");
                Time arrTime = rs.getTime("arrtime");
                Date depDate = rs.getDate("depdate");
                Date arrDate = rs.getDate("arrdate");
                int price = rs.getInt("price");
                String currency = rs.getString("currency");
                String gateNr = rs.getString("gatenr");

                LocalTime departureTime = depTime.toLocalTime();
                LocalTime arrivalTime = arrTime.toLocalTime();
                LocalDate departureDate = depDate.toLocalDate();
                LocalDate arrivalDate = arrDate.toLocalDate();
                int planeId = -1;
                String model = null;
                int numberOfSeats = -1;
                // flights.add(new FlightData(id, depAirport, arrAirport, departureDate,
                // arrivalDate, departureTime, arrivalTime, price, gateNr));
                for (Map.Entry<Integer, Integer> entry : ids.entrySet()) {
                    if (id == entry.getKey()) {
                        for (PlaneData p : planes) {
                            if (entry.getValue() == p.id()) {
                                planeId = p.id();
                                model = p.model();
                                numberOfSeats = p.numberOfSeats();
                            }
                        }
                    }
                }
                PlaneData planeData = new PlaneData(planeId, model, numberOfSeats);
                flights.add(new FlightData(id, depAirport, arrAirport, departureDate, arrivalDate, departureTime,
                        arrivalTime, price, currency, gateNr, planeData));
            }

            rs.close();
            ps.close();
            conn.close();

            return flights;
        } catch (SQLException e) {
            throw new FlightPersistenceException("P: Couldn't get all flights.", e);
        }

    }

    private List<PlaneData> getPlanesForFlights() throws SQLException {
        List<PlaneData> results = new ArrayList<>();

        Connection conn = db.getConnection();

        String sql = "SELECT * FROM plane WHERE id IN ("
                + "SELECT planeid FROM flightPlane)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("id");
            String model = rs.getString("model");
            int numberOfSeats = rs.getInt("numberofseats");
            results.add(new PlaneData(id, model, numberOfSeats));
        }
        rs.close();
        ps.close();
        conn.close();
        return results;
    }

    private HashMap<Integer, Integer> getFlightAndPlaneIds() throws SQLException {
        HashMap<Integer, Integer> ids = new HashMap<>();
        Connection conn = db.getConnection();
        String sql = "SELECT * FROM flightPlane";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ids.put(rs.getInt("flightid"), rs.getInt("planeid"));
        }
        rs.close();
        ps.close();
        conn.close();
        return ids;
    }

    @Override
    public List<FlightData> getFlight(String departure, String arrival, Date date) throws FlightPersistenceException {

        List<FlightData> flightsFound = new ArrayList<>();
        List<Integer> flightIds = new ArrayList<>();

        HashMap<Integer, Integer> allPlaneFlightIds = new HashMap<>();

        try {
            Connection conn = db.getConnection();
            String sql = "SELECT flightid FROM flight WHERE departure = ? and arrival = ? and depdate = ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setString(1, departure);
            stm.setString(2, arrival);
            stm.setDate(3, date);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("flightid");
                flightIds.add(id);
            }

            if (flightIds.isEmpty()) {
                System.out.println("Empty");
            }

            allPlaneFlightIds = getFlightAndPlaneIds();
            for (Integer i : flightIds) {
                if (allPlaneFlightIds.containsKey(i)) {
                    String planeSql = "SELECT * FROM plane WHERE id = ?";
                    PreparedStatement pstm = conn.prepareStatement(planeSql);
                    pstm.setInt(1, allPlaneFlightIds.get(i));
                    ResultSet resultSet = pstm.executeQuery();

                    while (resultSet.next()) {
                        String model = resultSet.getString("model");
                        int numberOfSeats = resultSet.getInt("numberofseats");
                        int planeId = resultSet.getInt("id");
                        PlaneData planeData = new PlaneData(planeId, model, numberOfSeats);

                        String flightSql = "SELECT * FROM flight WHERE departure = ? and arrival = ? and depdate = ?";
                        PreparedStatement ps = conn.prepareStatement(flightSql);
                        ps.setString(1, departure);
                        ps.setString(2, arrival);
                        ps.setDate(3, date);

                        ResultSet flightRs = ps.executeQuery();

                        while (flightRs.next()) {

                            int id = flightRs.getInt("flightid");
                            Time depTime = flightRs.getTime("deptime");
                            Time arrTime = flightRs.getTime("arrtime");
                            Date depDate = flightRs.getDate("depdate");
                            Date arrDate = flightRs.getDate("arrDate");
                            int price = flightRs.getInt("price");
                            String currency = flightRs.getString("currency");
                            String gateNr = flightRs.getString("gatenr");

                            LocalTime localDepTime = depTime.toLocalTime();
                            LocalTime localArrTime = arrTime.toLocalTime();
                            LocalDate localDepDate = depDate.toLocalDate();
                            LocalDate localArrDate = arrDate.toLocalDate();

                            FlightData flightData = new FlightData(id, departure, arrival, localDepDate, localArrDate,
                                    localDepTime, localArrTime, price, currency, gateNr, planeData);

                            flightsFound.add(flightData);
                        }

                    }

                }
            }
        } catch (SQLException e) {
            throw new FlightPersistenceException("P: Couldn't retrieve flight and plane ids from DB.", e);
        }

        return flightsFound;
    }

}
