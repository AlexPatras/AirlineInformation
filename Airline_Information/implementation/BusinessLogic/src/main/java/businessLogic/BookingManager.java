package businessLogic;

import java.util.regex.Pattern;

import dataRecords.BookingData;
import persistence.BookingDAO;
import persistence.BookingNonDAO;
import persistence.BookingNonDAOImpl;
import persistence.BookingPersistenceException;
import persistence.DAO;
import persistence.InputValidationException;

public class BookingManager {

    private DAO<BookingData> bookingDAO = new BookingDAO();
    private BookingNonDAO bookingNonDAO = new BookingNonDAOImpl();

    public void create(Booking booking) throws BookingPersistenceException, InputValidationException {

        if (booking.getFlightID() == 0) {
            throw new InputValidationException("Provide the flight id.");
        } else {
            try {
                bookingDAO.create(booking.getBookingData());
            } catch (BookingPersistenceException e) {
                throw new BookingPersistenceException("BL: Couldn't create booking.", e);
            } catch (InputValidationException e) {
                throw new BookingPersistenceException("BL: Couldn't create booking.", e);
            } catch (Exception e) {
                throw new BookingPersistenceException("BL: Couldn't create booking.", e);
            }
        }

    }

    public BookingData read(int id) throws BookingPersistenceException {
        try {
            return bookingDAO.read(id);
        } catch (BookingPersistenceException e) {
            throw new BookingPersistenceException("Couldn't get booking information.", e);
        } catch (Exception e) {
            throw new BookingPersistenceException("Couldn't get booking information.", e);
        }
    }

    public void setBookingDAO(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public int getDefaultBookingPrice(int id) throws BookingPersistenceException {
        try {
            return bookingNonDAO.getDefaultBookingPrice(id);
        } catch (BookingPersistenceException e) {
            throw new BookingPersistenceException("Couldn't get default booking price.", e);
        }
    }

    public int getInsertedBookingId() throws BookingPersistenceException {
        try {
            return bookingNonDAO.getInsertedBookingId();
        } catch (BookingPersistenceException e) {
            throw new BookingPersistenceException("Couldn't get inserted booking ID.");
        }
    }
}
