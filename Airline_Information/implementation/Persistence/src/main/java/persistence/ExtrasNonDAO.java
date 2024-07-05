package persistence;

import java.util.ArrayList;

import dataRecords.ExtrasData;

public interface ExtrasNonDAO {
    ArrayList<Integer> getExtrasIds(int flightId) throws ExtrasPersistenceException;
    ExtrasData getExtras(int extrasId) throws ExtrasPersistenceException;
    void createBookingExtras(int extrasId, int bookingId) throws ExtrasPersistenceException;
    int getExtrasPrice(int extrasId) throws ExtrasPersistenceException;
}
