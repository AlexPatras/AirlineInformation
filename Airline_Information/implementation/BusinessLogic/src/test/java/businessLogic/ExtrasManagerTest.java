package businessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dataRecords.ExtrasData;
import persistence.BookingNonDAO;
import persistence.BookingPersistenceException;
import persistence.ExtrasNonDAO;
import persistence.ExtrasPersistenceException;

public class ExtrasManagerTest {
    @Mock
    private ExtrasNonDAO extrasNonDAO;

    private BookingNonDAO bookingNonDAO;

    private ExtrasManager extrasManager;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        extrasManager = new ExtrasManager();

        extrasNonDAO = mock(ExtrasNonDAO.class);
        bookingNonDAO = mock(BookingNonDAO.class);

        ((ExtrasManager) extrasManager).setExtrasNonDao(extrasNonDAO);
        ((ExtrasManager) extrasManager).setBookingNonDao(bookingNonDAO);
    }

    @Test
    public void testGetExtrasIds() throws ExtrasPersistenceException{

        int flightId = 1;

        ArrayList<Integer> extrasIds = new ArrayList<>();
        extrasIds.add(1);
        extrasIds.add(2);

        when(extrasNonDAO.getExtrasIds(flightId)).thenReturn(extrasIds);

        ArrayList<Integer> result = extrasManager.getExtrasIds(flightId);

        assertEquals(extrasIds, result);
    }

    @Test
    public void testGetExtrasIdsExtrasPersistenceException()
            throws ExtrasPersistenceException {
        int flightId = 1;

        doThrow(ExtrasPersistenceException.class).when(extrasNonDAO).getExtrasIds(flightId);

        assertThrows(ExtrasPersistenceException.class, () -> extrasManager.getExtrasIds(flightId));
    }

    @Test
    public void testGetExtras() throws ExtrasPersistenceException {
        int extrasId = 1;

        ExtrasData expectedExtrasData = new ExtrasData(1, "Extra legroom", 50);
        Extras expectedExtras = new Extras(expectedExtrasData);

        when(extrasNonDAO.getExtras(extrasId)).thenReturn(expectedExtrasData);

        Extras extrasData = extrasManager.getExtras(extrasId);

        assertEquals(expectedExtras.toString(), extrasData.toString());
    }

    @Test
    public void testGetExtrasExtrasPersistenceException() throws ExtrasPersistenceException {
        int extrasId = 1;

        doThrow(ExtrasPersistenceException.class).when(extrasNonDAO).getExtras(extrasId);

        assertThrows(ExtrasPersistenceException.class, () -> extrasManager.getExtras(extrasId));
    }

    @Test
    public void testCreateBookingExtras() throws ExtrasPersistenceException, BookingPersistenceException {
        when(bookingNonDAO.getInsertedBookingId()).thenReturn(1);
        doNothing().when(extrasNonDAO).createBookingExtras(1, 1);

        

        extrasManager.createBookingExtras(1);

        verify(extrasNonDAO, times(1)).createBookingExtras(1, 1);
    }

    @Test
    public void testCreateBookingExtrasExtrasPersistenceException() throws ExtrasPersistenceException, BookingPersistenceException {
        when(bookingNonDAO.getInsertedBookingId()).thenReturn(1);
        doThrow(new ExtrasPersistenceException("")).when(extrasNonDAO).createBookingExtras(1, 1);

        assertThrows(ExtrasPersistenceException.class, () -> extrasManager.createBookingExtras(1));
    }

}
