package businessLogic;

import persistence.BookingNonDAO;
import persistence.PersistenceAPI;

public class BusinessLogicAPIImpl implements BusinessLogicAPI {

    final PersistenceAPI persistenceAPI;

    BusinessLogicAPIImpl (PersistenceAPI persistenceAPI) {
        this.persistenceAPI = persistenceAPI;
    }

    @Override
    public PlaneManager getPlaneManager() {
        return new PlaneManager ();
    }

    @Override
    public FlightManager getFlightManager() {
        return new FlightManager();
    }

    @Override
    public AirportManager getAirportManager() {
        return new AirportManagerImpl();
    }

    public DiscountManager getDiscountManager() {
        return new DiscountManagerImpl();
    }

    @Override
    public UserManager getUserManager() {
        return new UserManagerImpl();
    }

    @Override
    public BookingManager getBookingManager() {
        return new BookingManager();
    }

    @Override
    public PassengerManager getPassengerManager() {
        return new PassengerManager();
    }

    @Override
    public ExtrasManager getExtrasManager() {
        return new ExtrasManager();
    }

    @Override
    public BookingNonDAO getBookingNonDAO() {
        return this.persistenceAPI.getSecondaryBookingStorageService();
    }
}