package businessLogic;

import java.util.regex.Pattern;

import dataRecords.PassengerData;
import persistence.DAO;
import persistence.InputValidationException;
import persistence.PassengerDAO;
import persistence.PassengerNonDAO;
import persistence.PassengerNonDAOImpl;
import persistence.PassengerPersistenceException;

public class PassengerManager {

    private DAO<PassengerData> passengerDao = new PassengerDAO();
    private PassengerNonDAO passengerNonDAO = new PassengerNonDAOImpl();

    public void create(Passenger passenger) throws PassengerPersistenceException, InputValidationException {
        if (passenger.getPassengerData().email().isEmpty()) {
            throw new InputValidationException("BL: The email is required.");
        }

        if (!isValidEmail(passenger.getPassengerData().email())) {
            throw new InputValidationException("BL: The email must be valid.");
        }

        if (passenger.getPassengerData().firstName().isEmpty()) {
            throw new InputValidationException("Please provide first name.");
        } else if (!passenger.getPassengerData().firstName().matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid first name. Only letters and hyphens allowed.");
        } else if (passenger.getPassengerData().firstName().length() > 30) {
            throw new InputValidationException("Invalid first name. Too long.");
        }
        if (passenger.getPassengerData().lastName().isEmpty()) {
            throw new InputValidationException("Please provide the last name.");
        } else if (!passenger.getPassengerData().lastName().matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid last name. Only letters and hyphens allowed.");
        } else if (passenger.getPassengerData().lastName().length() > 30) {
            throw new InputValidationException("Invalid last name. Too long.");
        }

        try {
            passengerDao.create(passenger.getPassengerData());
        } catch (PassengerPersistenceException e) {
            throw new PassengerPersistenceException("BL: Couldn't create passenger.", e);
        } catch (Exception e) {
            throw new PassengerPersistenceException("BL: Couldn't create passenger.", e);
        }
    }

    public Passenger read(int id) throws PassengerPersistenceException {
        try {
            return new Passenger(passengerDao.read(id));
        } catch (PassengerPersistenceException e) {
            throw new PassengerPersistenceException("BL: Couldn't read passenger.", e);
        } catch (Exception e) {
            throw new PassengerPersistenceException("BL: Couldn't read passenger.", e);
        }
    }

    public void update(Passenger passenger) throws PassengerPersistenceException, InputValidationException {

        if (passenger.getEmail().isEmpty()) {
            throw new InputValidationException("BL: The email is required");
        }

        if (!isValidEmail(passenger.getEmail())) {
            throw new InputValidationException("The email must be valid.");
        }

        if (passenger.getFirstName().isEmpty()) {
            throw new InputValidationException("Please provide first name.");
        } else if (!passenger.getFirstName().matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid first name. Only letters and hyphens allowed.");
        } else if (passenger.getFirstName().length() > 30) {
            throw new InputValidationException("Invalid first name. Too long.");
        }
        if (passenger.getLastName().isEmpty()) {
            throw new InputValidationException("Please provide the last name.");
        } else if (!passenger.getLastName().matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid last name. Only letters and hyphens allowed.");
        } else if (passenger.getLastName().length() > 30) {
            throw new InputValidationException("Invalid last name. Too long.");
        }

        try {
            passengerDao.update(passenger.getPassengerData());
        } catch (PassengerPersistenceException e) {
            throw new PassengerPersistenceException("BL: Couldn't update passenger.", e);
        } catch (Exception e) {
            throw new PassengerPersistenceException("BL: Couldn't update passenger", e);
        }
    }

    public int checkForExistingPassenger(Passenger passenger)
            throws PassengerPersistenceException, InputValidationException {
        if (passenger.getEmail().isEmpty()) {
            throw new InputValidationException("BL: The email is required");
        }

        if (!isValidEmail(passenger.getEmail())) {
            throw new InputValidationException("The email must be valid.");
        }

        if (passenger.getFirstName().isEmpty()) {
            throw new InputValidationException("Please provide first name.");
        } else if (!passenger.getFirstName().matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid first name. Only letters and hyphens allowed.");
        } else if (passenger.getFirstName().length() > 30) {
            throw new InputValidationException("Invalid first name. Too long.");
        }
        if (passenger.getLastName().isEmpty()) {
            throw new InputValidationException("Please provide the last name.");
        } else if (!passenger.getLastName().matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid last name. Only letters and hyphens allowed.");
        } else if (passenger.getLastName().length() > 30) {
            throw new InputValidationException("Invalid last name. Too long.");
        }

        try {
            return passengerNonDAO.checkForExistingPassenger(passenger.getPassengerData());
        } catch (PassengerPersistenceException e) {
            throw new PassengerPersistenceException("BL: Couldn't check for existing passengers.", e);
        }
    }

    public int getNextFreeId() throws PassengerPersistenceException {
        try {
            return passengerNonDAO.getNextFreeId();
        } catch (PassengerPersistenceException e) {
            throw new PassengerPersistenceException("BL: Couldn't get next free passenger id.", e);
        }
    }

    public PassengerData create(String firstName, String lastName, String email, String phoneNumber, String nationality,
            String docNumber, String birthDay) throws PassengerPersistenceException, InputValidationException {

        if (email.isEmpty()) {
            throw new InputValidationException("BL: The email is required");
        }

        if (!isValidEmail(email)) {
            throw new InputValidationException("The email must be valid.");
        }

        if (firstName.isEmpty()) {
            throw new InputValidationException("Please provide first name.");
        } else if (!firstName.matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid first name. Only letters and hyphens allowed.");
        } else if (firstName.length() > 30) {
            throw new InputValidationException("Invalid first name. Too long.");
        }
        if (lastName.isEmpty()) {
            throw new InputValidationException("Please provide the last name.");
        } else if (!lastName.matches("[a-zA-Z\\-]+")) {
            throw new InputValidationException("Invalid last name. Only letters and hyphens allowed.");
        } else if (lastName.length() > 30) {
            throw new InputValidationException("Invalid last name. Too long.");
        }

        try {
            return passengerNonDAO.create(firstName, lastName, email, phoneNumber, nationality, docNumber, birthDay);
        } catch (PassengerPersistenceException e) {
            throw new PassengerPersistenceException("BL: Couldn't create a new passenger.", e);
        }
    }

    public void setPassengerNonDao(PassengerNonDAO passengerNonDAO) {
        this.passengerNonDAO = passengerNonDAO;
    }

    public void setPassengerDao(PassengerDAO passengerDAO) {
        this.passengerDao = passengerDAO;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

}
