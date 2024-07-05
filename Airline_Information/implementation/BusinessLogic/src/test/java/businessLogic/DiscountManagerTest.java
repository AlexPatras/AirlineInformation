 package businessLogic;

 import org.assertj.core.api.ThrowableAssert;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import persistence.*;

 import java.lang.reflect.Field;
 import java.time.LocalDate;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import static org.assertj.core.api.Assertions.assertThatCode;
 import static org.assertj.core.api.Assertions.assertThatThrownBy;
 import static org.mockito.ArgumentMatchers.anyString;
 import static org.mockito.Mockito.*;

 public class DiscountManagerTest {

     private DiscountManager discountManager;
     private FlightNonDAO flightNonDAO;
     private BookingNonDAO bookingNonDAO;
     private BookingFlightNonDAO bookingFlightNonDAO;
     private FlightDiscountNonDAO flightDiscountNonDAO;
     private APIClient apiClient;

     @BeforeEach
     public void setup() {
         discountManager = new DiscountManagerImpl();
         flightNonDAO = mock(FlightNonDAOImpl.class);
         bookingNonDAO = mock(BookingNonDAOImpl.class);
         bookingFlightNonDAO = mock(BookingFlightNonDAOImpl.class);
         flightDiscountNonDAO = mock(FlightDiscountNonDAOImpl.class);
         apiClient = mock(APIClientImpl.class);
     }

     @Test
     public void testApplyDiscountToLocation() throws IllegalAccessException, DiscountNotAppliedException, DiscountDatabaseException {
         // Arrange

         Field[] fields = DiscountManagerImpl.class.getDeclaredFields();
         for (Field field : fields) {
             field.setAccessible(true);
             String fieldName = field.getName();

             if (fieldName.equals("flightNonDAO")) {
                 field.set(discountManager, flightNonDAO);
             } else if (fieldName.equals("bookingNonDAO")) {
                 field.set(discountManager, bookingNonDAO);
             } else if (fieldName.equals("bookingFlightNonDAO")) {
                 field.set(discountManager, bookingFlightNonDAO);
             } else if (fieldName.equals("flightDiscountNonDAO")) {
                 field.set(discountManager, flightDiscountNonDAO);
             } else if (fieldName.equals("apiClient")) {
                 field.set(discountManager, apiClient);
             }
         }

         // Training mocks

         String destination = "London";
         String discountMode = "Sun hours";
         LocalDate testDate = LocalDate.now();
         List<Double> mockCoordinates = List.of(51.5085300, 0.1257400);
         String latitude = String.valueOf(mockCoordinates.get(0));
         String longitude = String.valueOf(mockCoordinates.get(1));
         when(apiClient.getCoordinates(destination)).thenReturn(mockCoordinates);
         when(apiClient.fetchWeather(latitude, longitude, testDate, discountMode)).thenReturn(15D);
         HashMap<Integer, Integer> flightIdsAndPrices = new HashMap<>();
         flightIdsAndPrices.put(1, 100);
         when(flightNonDAO.getFlightIdsAndPrices(anyString())).thenReturn(flightIdsAndPrices);
         HashMap<Integer, Integer> bookingIdsAndPrices = new HashMap<>();
         bookingIdsAndPrices.put(1, 20);
         when(bookingNonDAO.getBookingIdsAndPrices(flightIdsAndPrices)).thenReturn(bookingIdsAndPrices);
         HashMap<Integer, Integer> bookingAndFlightIds = new HashMap<>();
         bookingAndFlightIds.put(1, 1);
         when(bookingFlightNonDAO.getBookingAndFlightIds(flightIdsAndPrices)).thenReturn(bookingAndFlightIds);
         doNothing().when(bookingNonDAO).updateBookingPrices(bookingAndFlightIds, flightIdsAndPrices, bookingIdsAndPrices, 0.15);
         doNothing().when(flightDiscountNonDAO).updateCurrentDiscounts(flightIdsAndPrices);

         // Acting

         discountManager.applyDiscountToLocation(destination, discountMode);

         ThrowableAssert.ThrowingCallable code = () -> {
             discountManager.applyDiscountToLocation(destination, discountMode);
         };

         // Verifications and assertions

         verify(flightNonDAO).getFlightIdsAndPrices(destination);
         verify(apiClient).getCoordinates(destination);
         verify(apiClient).fetchWeather(anyString(), anyString(), any(LocalDate.class), anyString());
         verify(bookingNonDAO).getBookingIdsAndPrices(flightIdsAndPrices);
         verify(bookingFlightNonDAO).getBookingAndFlightIds(flightIdsAndPrices);
         verify(bookingNonDAO).updateBookingPrices(any(HashMap.class), any(HashMap.class), any(HashMap.class), anyDouble());
         verify(flightDiscountNonDAO).updateCurrentDiscounts(flightIdsAndPrices);

         assertThatCode(code).doesNotThrowAnyException();
     }

     @Test
     public void testApplyDiscountToLocationThrowsException() {
         String destination = "";
         String discountMode = "Sun hours";
         ThrowableAssert.ThrowingCallable code = () -> {
             discountManager.applyDiscountToLocation(destination, discountMode);
         };

         assertThatThrownBy(code).isInstanceOf(DiscountNotAppliedException.class);
     }

     @Test
     public void testApplyDiscountToFlight() throws IllegalAccessException, DiscountDatabaseException, DiscountNotAppliedException {
         // Arrange

         Field[] fields = DiscountManagerImpl.class.getDeclaredFields();
         for (Field field : fields) {
             field.setAccessible(true);
             String fieldName = field.getName();

             if (fieldName.equals("flightNonDAO")) {
                 field.set(discountManager, flightNonDAO);
             } else if (fieldName.equals("bookingNonDAO")) {
                 field.set(discountManager, bookingNonDAO);
             } else if (fieldName.equals("bookingFlightNonDAO")) {
                 field.set(discountManager, bookingFlightNonDAO);
             } else if (fieldName.equals("flightDiscountNonDAO")) {
                 field.set(discountManager, flightDiscountNonDAO);
             } else if (fieldName.equals("apiClient")) {
                 field.set(discountManager, apiClient);
             }
         }


         // Training mocks

         int flightId = 1;
         LocalDate testDate = LocalDate.now();
         String discountMode = "Sun hours";
         Map<Integer, List<String>> flightInfo = new HashMap<>();
         flightInfo.put(flightId, List.of("London", "200"));
         when(flightNonDAO.checkFlightForDiscount(flightId)).thenReturn(flightInfo);

         List<Double> mockCoordinates = List.of(51.5085300, 0.1257400);
         when(apiClient.getCoordinates("London")).thenReturn(mockCoordinates);

         String latitude = String.valueOf(mockCoordinates.get(0));
         String longitude = String.valueOf(mockCoordinates.get(1));

         when(apiClient.fetchWeather(latitude, longitude, testDate, discountMode)).thenReturn(15D);

         HashMap<Integer, Integer> flightIdsAndPrices = new HashMap<>();
         int price = Integer.parseInt(flightInfo.get(flightId).get(1));
         flightIdsAndPrices.put(flightId, price);

         HashMap<Integer, Integer> bookingIdsAndPrices = new HashMap<>();
         bookingIdsAndPrices.put(1, 20);
         when(bookingNonDAO.getBookingIdsAndPrices(flightIdsAndPrices)).thenReturn(bookingIdsAndPrices);
         HashMap<Integer, Integer> bookingAndFlightIds = new HashMap<>();
         bookingAndFlightIds.put(1, 1);
         when(bookingFlightNonDAO.getBookingAndFlightIds(flightIdsAndPrices)).thenReturn(bookingAndFlightIds);
         doNothing().when(bookingNonDAO).updateBookingPrices(bookingAndFlightIds, flightIdsAndPrices, bookingIdsAndPrices, 0.15);
         doNothing().when(flightDiscountNonDAO).updateCurrentDiscounts(flightIdsAndPrices);

         // Acting

         discountManager.applyDiscountToFlight(flightId, discountMode);

         ThrowableAssert.ThrowingCallable code = () -> {
             discountManager.applyDiscountToFlight(flightId, discountMode);
         };

         // Verifications and assertions

         verify(flightNonDAO).checkFlightForDiscount(flightId);
         verify(apiClient).getCoordinates("London");
         verify(apiClient).fetchWeather(anyString(), anyString(), any(LocalDate.class), anyString());
         verify(bookingNonDAO).getBookingIdsAndPrices(flightIdsAndPrices);
         verify(bookingFlightNonDAO).getBookingAndFlightIds(flightIdsAndPrices);
         verify(bookingNonDAO).updateBookingPrices(any(HashMap.class), any(HashMap.class), any(HashMap.class), anyDouble());
         verify(flightDiscountNonDAO).updateCurrentDiscounts(flightIdsAndPrices);

         assertThatCode(code).doesNotThrowAnyException();
     }

     @Test
     public void testApplyDiscountToFlightThrowsException() {
         int flightId = 1;
         String discountMode = "";
         ThrowableAssert.ThrowingCallable code = () -> {
             discountManager.applyDiscountToFlight(flightId, discountMode);
         };

         assertThatThrownBy(code).isInstanceOf(DiscountNotAppliedException.class);
     }

     @Test
     public void testCalculatePrice() throws IllegalAccessException, DiscountNotAppliedException, DiscountDatabaseException {
         // Arrange

         Field[] fields = DiscountManagerImpl.class.getDeclaredFields();
         for (Field field : fields) {
             field.setAccessible(true);
             String fieldName = field.getName();

             if (fieldName.equals("flightNonDAO")) {
                 field.set(discountManager, flightNonDAO);
             } else if (fieldName.equals("bookingNonDAO")) {
                 field.set(discountManager, bookingNonDAO);
             } else if (fieldName.equals("bookingFlightNonDAO")) {
                 field.set(discountManager, bookingFlightNonDAO);
             } else if (fieldName.equals("flightDiscountNonDAO")) {
                 field.set(discountManager, flightDiscountNonDAO);
             } else if (fieldName.equals("apiClient")) {
                 field.set(discountManager, apiClient);
             }
         }

         // Training mocks

         when(apiClient.getCoordinates(anyString())).thenReturn(List.of(12.12, 15.15));
         when(apiClient.fetchWeather(anyString(), anyString(), any(LocalDate.class), anyString())).thenReturn(10D);
         HashMap<Integer, List<String>> flightInfo1 = new HashMap<>();
         flightInfo1.put(1, List.of("London", "120"));
         when(flightNonDAO.checkIfFlightHasDiscount(1)).thenReturn(flightInfo1);

         HashMap<Integer, Integer> flightIdsAndPrices = new HashMap<>();
         flightIdsAndPrices.put(1, Integer.parseInt(flightInfo1.get(1).get(1)));

         HashMap<Integer, Integer> bookingIdsAndPrices = new HashMap<>();
         bookingIdsAndPrices.put(1, 20);
         when(bookingNonDAO.getBookingIdsAndPrices(flightIdsAndPrices)).thenReturn(bookingIdsAndPrices);
         HashMap<Integer, Integer> bookingAndFlightIds = new HashMap<>();
         bookingAndFlightIds.put(1, 1);
         when(bookingFlightNonDAO.getBookingAndFlightIds(flightIdsAndPrices)).thenReturn(bookingAndFlightIds);

         doNothing().when(bookingNonDAO).calculateSingleBookingPrice(1, 1);

         // Acting

         int bookingId = 1;
         int flightId = 1;

        try {
            discountManager.calculatePrice(bookingId ,flightId);
        } catch (BookingPersistenceException e) {
            throw new RuntimeException(e);
        }

         ThrowableAssert.ThrowingCallable code = () -> {
             discountManager.calculatePrice(bookingId, flightId);
         };

         verify(apiClient).getCoordinates(anyString());
         verify(apiClient).fetchWeather(anyString(), anyString(), any(LocalDate.class), anyString());
         verify(flightNonDAO).checkIfFlightHasDiscount(anyInt());
         verify(bookingNonDAO).calculateSingleBookingPrice(anyInt(), anyInt());


         assertThatCode(code).doesNotThrowAnyException();


     }

 }
