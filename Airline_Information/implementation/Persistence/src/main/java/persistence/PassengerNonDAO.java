package persistence;

import dataRecords.PassengerData;

public interface PassengerNonDAO {
    int getNextFreeId() throws PassengerPersistenceException;
    PassengerData create(String firstName, String lastName, String email, String phoneNumber, String nationality, String docNumber, String birthDay) throws PassengerPersistenceException;
    int checkForExistingPassenger(PassengerData passengerData) throws PassengerPersistenceException;
}
