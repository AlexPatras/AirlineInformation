package businessLogic;

import persistence.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which applies dynamic discount to either specific flight or to a bunch of flights and also calculates the price of newly created booking
 */
public class DiscountManagerImpl implements DiscountManager {

    private final FlightNonDAO flightNonDAO = new FlightNonDAOImpl();
    private final BookingNonDAO bookingNonDAO = new BookingNonDAOImpl();
    private final BookingFlightNonDAO bookingFlightNonDAO = new BookingFlightNonDAOImpl();
    private final FlightDiscountNonDAO flightDiscountNonDAO = new FlightDiscountNonDAOImpl();
    private final APIClient apiClient = new APIClientImpl();
    /**
     * Applies discount to all flights with same arrival city, depending on what airport name is passed.
     * @param destination represents the name of location to which the discount will be applied (All flights with
     * this destination set as their arrival place will get a discount
     * @param discountMode determines based on what the discount should be calculated
     */
    @Override
    public void applyDiscountToLocation(String destination, String discountMode) throws DiscountNotAppliedException, DiscountDatabaseException {
        if (discountMode == null || discountMode.equals("") || destination == null || destination.equals("")) {
            throw new DiscountNotAppliedException("Discount cannot be calculated because of the lack of parameters for it");
        }
            HashMap<Integer, Integer> flightIdsAndPrices = this.flightNonDAO.getFlightIdsAndPrices(destination);
            List<Double> coordinates = apiClient.getCoordinates(destination);

            String lat = String.valueOf(coordinates.get(0));
            String lon = String.valueOf(coordinates.get(1));

            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            double weatherCalculatedPrice = apiClient.fetchWeather(lat, lon, yesterday, discountMode);
            double percentage = weatherCalculatedPrice / 100;
            double discountPercentage = Math.round(percentage * 10.0) / 10.0;

            HashMap<Integer, Integer> bookingIdsAndPrices = this.bookingNonDAO.getBookingIdsAndPrices(flightIdsAndPrices);
            HashMap<Integer, Integer> bookingAndFlightIds = this.bookingFlightNonDAO.getBookingAndFlightIds(flightIdsAndPrices);
            this.bookingNonDAO.updateBookingPrices(bookingAndFlightIds, flightIdsAndPrices, bookingIdsAndPrices, discountPercentage);
            this.flightDiscountNonDAO.updateCurrentDiscounts(flightIdsAndPrices);
    }


    /**
     * Applies discount to a specific flight, based on flight id that is passed.
     * @param flightId determines which flight will have a discount applied
     * @param discountMode determines based on what the discount should be calculated
     * @throws DiscountNotAppliedException if exception type of DiscountDatabaseException is caught
     */
    @Override
    public void applyDiscountToFlight(int flightId, String discountMode) throws DiscountDatabaseException, DiscountNotAppliedException {
        if (discountMode == null || discountMode.equals("")) {
            throw new DiscountNotAppliedException("Discount cannot be calculated because of the lack of parameters for it");
        }
            Map<Integer, List<String>> flightInfo = this.flightNonDAO.checkFlightForDiscount(flightId);
            HashMap<Integer, Integer> flightIdAndPrice = new HashMap<>();
            String location = flightInfo.get(flightId).get(0);

            int price = Integer.parseInt(flightInfo.get(flightId).get(1));
            System.out.println("price of flight " + flightId + ": " + price);

            flightIdAndPrice.put(flightId, price);

            List<Double> coordinates = apiClient.getCoordinates(location);

            String lat = String.valueOf(coordinates.get(0));
            String lon = String.valueOf(coordinates.get(1));
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            double weatherCalculatedPrice = apiClient.fetchWeather(lat, lon, yesterday, discountMode);
            double percentage = weatherCalculatedPrice / 100;
            double discountPercentage = Math.round(percentage * 10.0) / 10.0;

            HashMap<Integer, Integer> bookingIdsAndPrices = this.bookingNonDAO.getBookingIdsAndPrices(flightIdAndPrice);
            HashMap<Integer, Integer> bookingAndFlightIds = this.bookingFlightNonDAO.getBookingAndFlightIds(flightIdAndPrice);
            this.bookingNonDAO.updateBookingPrices(bookingAndFlightIds, flightIdAndPrice, bookingIdsAndPrices, discountPercentage);
            this.flightDiscountNonDAO.updateCurrentDiscounts(flightIdAndPrice);
    }

    @Override
    public void staticApplyDiscountToFlight(int flightId, String discountMode) throws DiscountDatabaseException, DiscountNotAppliedException {

    }


    /**
     * Produces a distinct list of flight arrival places
     * @return a list of Strings with various location names
     * @throws DiscountDatabaseException if exception type of DiscountDatabaseException is caught
     */
    @Override
    public List<String> getFlightLocations() throws DiscountDatabaseException {
            return flightNonDAO.getFlightDestinations();
    }

    /**
     * Calculates price of certain booking. Checks if booking has flight which is being discounted.
     * @param flightId gives information about which flight should be looked for
     * @throws DiscountDatabaseException if exception type of DiscountDatabaseException is caught
     */
    @Override
    public void calculatePrice(int bookingId, int flightId) throws DiscountNotAppliedException, DiscountDatabaseException, BookingPersistenceException{
            String discountMode = "Sun hours"; HashMap<Integer, Integer> flightIdAndPrice = new HashMap<>();
            HashMap<Integer, List<String>> flightInfo = this.flightNonDAO.checkIfFlightHasDiscount(flightId);
            String location;
            if (flightInfo.isEmpty()) {
                flightInfo = this.flightNonDAO.getSingleFlightInfo(flightId);
                int flightPrice = Integer.parseInt(flightInfo.get(flightId).get(1));
                flightPrice += bookingNonDAO.getDefaultBookingPrice(bookingId);
                flightIdAndPrice.put(flightId, flightPrice);
                bookingNonDAO.calculateSingleBookingPrice(bookingId, flightPrice);
            } else {
                int flightPrice = Integer.parseInt(flightInfo.get(flightId).get(1));
                location = flightInfo.get(flightId).get(0);
                flightIdAndPrice.put(flightId, flightPrice);
                List<Double> coordinates = apiClient.getCoordinates(location);
                String lat = String.valueOf(coordinates.get(0));
                String lon = String.valueOf(coordinates.get(1));
                LocalDate today = LocalDate.now();
                LocalDate yesterday = today.minusDays(1);

                double weatherCalculatedPercentage = apiClient.fetchWeather(lat, lon, yesterday, discountMode);
                double percentage = weatherCalculatedPercentage / 100;
                double discountPercentage = Math.round(percentage * 10.0) / 10.0;

                int finalPrice = (int) ((flightPrice) * (1 - discountPercentage));

                finalPrice += bookingNonDAO.getDefaultBookingPrice(bookingId);

                bookingNonDAO.calculateSingleBookingPrice(bookingId, finalPrice);

            }
    }
}
