package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dataRecords.BookingData;
import dataRecords.FlightData;
import dataRecords.PassengerData;

class PersistenceAPIImpl implements PersistenceAPI {

    @Override
    public PlaneNonDAO getPlaneStorageService() {
        return new PlaneNonDAOImpl();
    }

    @Override
    public AirportDAOImpl getAirportStorageService() {
        return new AirportDAOImpl();
    }

    @Override
    public DAO<BookingData> getBookingStorageService() {
        return new BookingDAO();
    }

    @Override
    public BookingNonDAO getSecondaryBookingStorageService() {
        return new BookingNonDAOImpl();
    }

    @Override
    public DAO<PassengerData> getPassengerStorageService() {
        return new PassengerDAO();
    }

    @Override
    public PassengerNonDAO getSecondaryPassengerStorageService() {
        return new PassengerNonDAOImpl();
    }

    @Override
    public ExtrasNonDAO getExtrasStorageService() {
        return new ExtrasNonDAOImpl();
    }

    @Override
    public FlightDAOImpl getFlightStorageService() {
        return new FlightDAOImpl();
    }

}
