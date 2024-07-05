package persTest;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import persistence.PersistenceAPI;
import persistence.PlaneNonDAO;

public class PersistenceAPITest {

    @Test
    public void testGetPlaneStorageService() {
        PersistenceAPI persistenceAPI = new PersistenceAPI() {
        };
        PlaneNonDAO userDAO = persistenceAPI.getPlaneStorageService();
        assertNull(userDAO);
    }

}
