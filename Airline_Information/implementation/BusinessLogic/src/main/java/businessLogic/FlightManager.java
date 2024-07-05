package businessLogic;

import dataRecords.AirportValidationException;
import dataRecords.DateTimeValidationException;
import dataRecords.FlightData;
import dataRecords.FlightDetailsValidationException;

import java.sql.Date;
import java.util.ArrayList;
import persistence.FlightDAOImpl;
import persistence.FlightNonDAOImpl;
import persistence.FlightPersistenceException;

import java.util.List;
import javax.sql.DataSource;
import persistence.FlightDAO;
import persistence.FlightNonDAO;

public class FlightManager {

    private FlightDAOImpl flightDAO = new FlightDAOImpl();
    private FlightNonDAO flightNonDAO = new FlightNonDAOImpl();

    public List<FlightData> getAllFlights() throws FlightPersistenceException {
        try {
            return flightNonDAO.getAllFlights();
        } catch (FlightPersistenceException e) {
            throw new FlightPersistenceException("BL: Couldn't get all flights.", e);
        }
    }

    public ArrayList<String> getIATACode() throws FlightPersistenceException {
        try {
            return flightNonDAO.getIATACode();
        } catch (FlightPersistenceException e) {
            throw new FlightPersistenceException("Couldn't get IATA codes", e);
        }
    }

    public ArrayList<String> getPlaneDetails() throws FlightPersistenceException {
        try {
            return flightNonDAO.getPlaneDetails();
        } catch (FlightPersistenceException e) {
            throw new FlightPersistenceException("Couldn't get model and number of seats");
        }
    }

    public void create(Flight flight) throws FlightPersistenceException, FlightDetailsValidationException, DateTimeValidationException, AirportValidationException {

        if (flight.getDepartureAirport().equals(flight.getArrivalAirport())) {
            throw new AirportValidationException("Departure airport can't be the same with arrival airport!");
        }
        
        if (!flight.getGateNr().matches(".*\\d.*")) {
        throw new FlightDetailsValidationException("The gate number should contain at least one digit!");

        } else if (flight.getGateNr().length() > 3 || flight.getGateNr().length() <= 0) {
            throw new FlightDetailsValidationException("Enter a valid number!");
        }

        if (flight.getDepartureDate().isAfter(flight.getArrivalDate())) {
            throw new DateTimeValidationException("The departure date can't be after arrival date!");
        }
        
        if (flight.getDepartureDate() != null && flight.getDepartureDate().isEqual(flight.getArrivalDate())
                && flight.getDepartureTime().getHour() > flight.getArrivalTime().getHour()) {
            throw new DateTimeValidationException("Departure time can't be after arrival time!");
        }

        if (flight.getDepartureDate() != null && flight.getDepartureDate().equals(flight.getArrivalDate())
                && flight.getDepartureTime().getHour() == flight.getArrivalTime().getHour()
                && flight.getDepartureTime().getMinute() > flight.getArrivalTime().getMinute()) {
            throw new DateTimeValidationException("Departure time can't be after arrival time!");
       
        }
        if(flight.getPrice() > 4500 || flight.getPrice() < 0  ){
            throw new FlightDetailsValidationException ("Enter a valid price!");
        }

        try {
            flightDAO.create(flight.getFlightData());

        } catch (FlightPersistenceException e) {
            throw new FlightPersistenceException("The flight couldn't be registered!", e);
        }
    }
    public List<FlightData> getFlight(String departure, String arrival, Date date) throws FlightBusinessException {
        try {
            return flightNonDAO.getFlight(departure, arrival, date);
        } catch (FlightPersistenceException e) {
            throw new FlightBusinessException("BL: Couldn't get flight.", e);
        }
    }

    public boolean CheckRegisterFlights(FlightData flightData, DataSource ds) throws FlightPersistenceException {
        try {
            return flightNonDAO.CheckRegisterFlights(flightData, ds);

        } catch (FlightPersistenceException e) {
            throw new FlightPersistenceException("Error checking flight existence!");
        }
    }

    public String getArrivalIATACode(int flightId) throws FlightPersistenceException {
        try {
            return flightNonDAO.getArrivalIATACode(flightId);
        } catch (FlightPersistenceException e) {
            throw new FlightPersistenceException("BL: Couldn't get arrival IATA code!", e);
        }
    }

    public void setFlightNonDao(FlightNonDAO flightNonDAO) {
        this.flightNonDAO = flightNonDAO;
    }

    public void setFlightDAO(FlightDAO flightDAO) {
        this.flightDAO = (FlightDAOImpl) flightDAO;
    }
}
