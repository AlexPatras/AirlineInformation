package persistence;

import dataRecords.BookingData;
import dataRecords.FlightData;
import dataRecords.PassengerData;

public interface PersistenceAPI {

    default PlaneNonDAO getPlaneStorageService() {
        return null;
    }

    default AirportDAOImpl getAirportStorageService() {
        return null;
    }

    default DAO<BookingData> getBookingStorageService() {
        return null;
    }

    default BookingNonDAO getSecondaryBookingStorageService() {
        return null;
    }
    
    default DAO<PassengerData> getPassengerStorageService() {
        return null;
    }

    default PassengerNonDAO getSecondaryPassengerStorageService() {
        return null;
    }

    default ExtrasNonDAO getExtrasStorageService() {
        return null;
    }
    default  FlightDAOImpl getFlightStorageService(){
        return null;
    }

}