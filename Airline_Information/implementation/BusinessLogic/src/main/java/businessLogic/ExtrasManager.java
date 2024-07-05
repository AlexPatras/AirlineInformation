package businessLogic;

import java.util.ArrayList;

import persistence.BookingNonDAO;
import persistence.BookingNonDAOImpl;
import persistence.BookingPersistenceException;
import persistence.ExtrasNonDAO;
import persistence.ExtrasNonDAOImpl;
import persistence.ExtrasPersistenceException;

public class ExtrasManager {

    private ExtrasNonDAO extrasNonDAO = new ExtrasNonDAOImpl();
    private BookingNonDAO bookingNonDAO = new BookingNonDAOImpl();

    public ArrayList<Integer> getExtrasIds(int flightId) throws ExtrasPersistenceException {
        try {
            return extrasNonDAO.getExtrasIds(flightId);
        } catch (ExtrasPersistenceException e) {
            throw new ExtrasPersistenceException("BL: Couldn't retrieve extras ids.", e);
        }
    }

    public Extras getExtras(int extrasId) throws ExtrasPersistenceException {
        try {
            return new Extras(extrasNonDAO.getExtras(extrasId));
        } catch (ExtrasPersistenceException e) {
            throw new ExtrasPersistenceException("BL: Couldn't retrieve extras.", e);
        }
    }

    public void createBookingExtras(int extrasId) throws ExtrasPersistenceException, BookingPersistenceException {
        try {
            extrasNonDAO.createBookingExtras(extrasId, bookingNonDAO.getInsertedBookingId());
        } catch (ExtrasPersistenceException e) {
            throw new ExtrasPersistenceException("BL: Couldn't add extras for this booking.", e);
        } catch (BookingPersistenceException e) {
            throw new BookingPersistenceException("BL: Couldn't get booking to add extras to.", e);
        }
    }

    public void setExtrasNonDao(ExtrasNonDAO extrasNonDAO) {
        this.extrasNonDAO = extrasNonDAO;
    }

    public void setBookingNonDao(BookingNonDAO bookingNonDAO) {
        this.bookingNonDAO = bookingNonDAO;
    }

    public int getExtrasPrice(int extrasId) throws ExtrasPersistenceException {
        try {
            return extrasNonDAO.getExtrasPrice(extrasId);
        } catch (ExtrasPersistenceException e) {
            throw new ExtrasPersistenceException("BL: Couldn't get extras price.", e);
        }
    }

}