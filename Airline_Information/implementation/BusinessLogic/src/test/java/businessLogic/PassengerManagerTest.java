package businessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dataRecords.PassengerData;
import persistence.InputValidationException;
import persistence.PassengerDAO;
import persistence.PassengerNonDAO;
import persistence.PassengerPersistenceException;

public class PassengerManagerTest {
        @Mock
        private PassengerDAO passengerDAO;

        private PassengerNonDAO passengerNonDAO;

        private PassengerManager passengerManager;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this);
                passengerManager = new PassengerManager();

                passengerDAO = mock(PassengerDAO.class);
                passengerNonDAO = mock(PassengerNonDAO.class);

                ((PassengerManager) passengerManager).setPassengerDao(passengerDAO);
                ((PassengerManager) passengerManager).setPassengerNonDao(passengerNonDAO);
        }

        @Test
        public void createPassenger() throws Exception {
                PassengerData passengerData = new PassengerData(
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);

                doNothing().when(passengerDAO).create(passengerData);

                // passengerManager.create(passengerData.firstName(), passengerData.lastName(),
                // passengerData.email(), passengerData.phoneNumber(),
                // passengerData.nationality(), passengerData.docNumber(),
                // passengerData.birthDay());

                passengerManager.create(passenger);

                verify(passengerDAO, times(1)).create(passengerData);
        }

        @Test
        public void testCreatePassengerPersistenceException() throws PassengerPersistenceException {
                PassengerData passengerData = new PassengerData(
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);
                doThrow(new PassengerPersistenceException("")).when(passengerDAO).create(passengerData);

                Executable executable = () -> passengerManager.create(passenger);

                Assertions.assertThrows(PassengerPersistenceException.class, executable);
        }

        @ParameterizedTest
        @CsvSource({
                        "'', Please provide first name.",
                        "12, Invalid first name. Only letters and hyphens allowed.",
                        "/a, Invalid first name. Only letters and hyphens allowed.",
                        "vdsbvdbhvdbhvdbhjvdbjdvvbjdvbdsbjvbjbjdbvjdbjdvbvdbdvjbdjbdvsbdjk, Invalid first name. Too long."
        })
        public void testCreatePassengerInvalidFirstName(String firstName, String expectedExceptionMessage)
                        throws InputValidationException, PassengerPersistenceException {
                if (firstName.equals("empty")) {
                        firstName = "";
                }
                PassengerData passengerData = new PassengerData(
                                firstName,
                                "Doe",
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);

                InputValidationException exception = assertThrows(InputValidationException.class,
                                () -> passengerManager.create(passenger));
                assertEquals(expectedExceptionMessage, exception.getMessage());

        }

        @ParameterizedTest
        @CsvSource({
                        "'', Please provide the last name.",
                        "12, Invalid last name. Only letters and hyphens allowed.",
                        "/a, Invalid last name. Only letters and hyphens allowed.",
                        "vdsbvdbhvdbhvdbhjvdbjdvvbjdvbdsbjvbjbjdbvjdbjdvbvdbdvjbdjbdvsbdjk, Invalid last name. Too long."
        })
        public void testCreatePassengerInvalidLastName(String lastName, String expectedExceptionMessage)
                        throws InputValidationException, PassengerPersistenceException {
                PassengerData passengerData = new PassengerData(
                                "John",
                                lastName,
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);

                InputValidationException exception = assertThrows(InputValidationException.class,
                                () -> passengerManager.create(passenger));
                assertEquals(expectedExceptionMessage, exception.getMessage());

        }

        @ParameterizedTest
        @CsvSource({
                        "'', BL: The email is required.",
                        "12, BL: The email must be valid.",
                        "/a, BL: The email must be valid.",
                        "vdsbvdbhvdbhvdbhjvdbjdvvbjdvbdsbjvbjbjdbvjdbjdvbvdbdvjbdjbdvsbdjk, BL: The email must be valid."
        })
        public void testCreatePassengerInvalidEmail(String email, String expectedExceptionMessage)
                        throws InputValidationException, PassengerPersistenceException {
                PassengerData passengerData = new PassengerData(
                                "John",
                                "Doe",
                                email,
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);

                InputValidationException exception = assertThrows(InputValidationException.class,
                                () -> passengerManager.create(passenger));
                assertEquals(expectedExceptionMessage, exception.getMessage());

        }

        @Test
        public void testReadPassenger() throws PassengerPersistenceException {

                int passengerId = 20;

                PassengerData passengerData = new PassengerData(
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");
                when(passengerDAO.read(passengerId)).thenReturn(passengerData);

                passengerManager.read(passengerId);

                verify(passengerDAO, times(1)).read(passengerId);
        }

        @Test
        public void testReadPassengerPersistenceException()
                        throws PassengerPersistenceException {
                int passengerId = 20;

                doThrow(new PassengerPersistenceException("")).when(passengerDAO).read(passengerId);

                Executable executable = () -> passengerManager.read(passengerId);

                assertThrows(PassengerPersistenceException.class, executable);
        }

        @Test
        public void testUpdatePassenger()
                        throws PassengerPersistenceException, InputValidationException {
                PassengerData passengerData = new PassengerData(
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);

                doNothing().when(passengerDAO).update(passengerData);

                passengerManager.update(passenger);

                verify(passengerDAO, times(1)).update(passengerData);
        }

        @Test
        public void testUpdatePassengerPersistenceException()
                        throws PassengerPersistenceException {
                PassengerData passengerData = new PassengerData(
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);

                doThrow(new PassengerPersistenceException("")).when(passengerDAO).update(passengerData);

                Executable executable = () -> passengerManager.update(passenger);

                assertThrows(PassengerPersistenceException.class, executable);
        }

        @Test
        public void testCheckForExistingPassenger()
                        throws PassengerPersistenceException, InputValidationException {
                PassengerData passengerData = new PassengerData(
                                "John",
                                "Doe",
                                "john.doe@example.com",
                                20,
                                "123456789",
                                "USA",
                                "ABCD1234",
                                "1990-01-01");

                Passenger passenger = new Passenger(passengerData);
                when(passengerNonDAO.checkForExistingPassenger(passengerData)).thenReturn(20);

                passengerManager.checkForExistingPassenger(passenger);

                verify(passengerNonDAO, times(1)).checkForExistingPassenger(passengerData);
        }

    @Test
    public void testGetNextFreeId() throws PassengerPersistenceException {
        when(passengerNonDAO.getNextFreeId()).thenReturn(1);

        passengerManager.getNextFreeId();

        verify(passengerNonDAO, times(1)).getNextFreeId();
    }
}
