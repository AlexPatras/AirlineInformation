package businessLogic;

import persistence.BookingNonDAO;

public interface BusinessLogicAPI {
    PlaneManager getPlaneManager();
    FlightManager getFlightManager();
    AirportManager getAirportManager();
    UserManager getUserManager();
    DiscountManager getDiscountManager();
    BookingManager getBookingManager();
    PassengerManager getPassengerManager();
    ExtrasManager getExtrasManager();
    BookingNonDAO getBookingNonDAO();
}
