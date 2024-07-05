package persTest;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import persistence.DiscountDatabaseException;
import persistence.FlightNonDAO;
import persistence.FlightNonDAOImpl;

import javax.sql.DataSource;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FlightNonDAOImplTest {

    private FlightNonDAO flightNonDAO;

    @Test
    public void testGetFlightIdsAndPrices() throws SQLException, IllegalAccessException, DiscountDatabaseException {
        flightNonDAO = new FlightNonDAOImpl();
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        HashMap<Integer, Integer> flightIdsAndPrices = new HashMap<>();
        flightIdsAndPrices.put(1, 200);
        flightIdsAndPrices.put(2, 120);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("flightid")).thenReturn(1, 2);
        when(rs.getInt("price")).thenReturn(200, 120);
        doNothing().when(rs).close();
        doNothing().when(ps).close();
        doNothing().when(conn).close();

        Field[] fields = FlightNonDAOImpl.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (fieldName.equals("db")) {
                field.set(flightNonDAO, ds);
            }
        }

        HashMap<Integer, Integer> results = flightNonDAO.getFlightIdsAndPrices("London");

        assertThat(results).isEqualTo(flightIdsAndPrices);
        verify(ds).getConnection();
        verify(conn).prepareStatement(anyString());
        verify(ps).setString(1, "London");
        verify(ps).executeQuery();
        verify(rs, times(3)).next();
        verify(rs, times(2)).getInt("flightid");
        verify(rs, times(2)).getInt("price");
        verify(rs).close();
        verify(ps).close();
        verify(conn).close();
    }

    @Test
    public void testGetFlightIdsAndPricesThrowsException() {
        flightNonDAO = new FlightNonDAOImpl();
        String location = "";
        ThrowableAssert.ThrowingCallable code = () -> {
            flightNonDAO.getFlightIdsAndPrices(location);
        };
        assertThatThrownBy(code).isInstanceOf(DiscountDatabaseException.class);
    }

    @Test
    public void testCheckFlightForDiscount() throws SQLException, IllegalAccessException, DiscountDatabaseException {
        flightNonDAO = new FlightNonDAOImpl();
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        Map<Integer, List<String>> flightInfo = new HashMap<>();
        flightInfo.put(1, List.of("London", "150"));

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("flightid")).thenReturn(1);
        when(rs.getString("arrival")).thenReturn("London");
        when(rs.getInt("price")).thenReturn(150);
        doNothing().when(rs).close();
        doNothing().when(ps).close();
        doNothing().when(conn).close();

        Field[] fields = FlightNonDAOImpl.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (fieldName.equals("db")) {
                field.set(flightNonDAO, ds);
            }
        }

        Map<Integer, List<String>> result = flightNonDAO.checkFlightForDiscount(1);

        assertThat(result).isEqualTo(flightInfo);

        verify(ds).getConnection();
        verify(conn).prepareStatement(anyString());
        verify(ps).setInt(1, 1);
        verify(ps).executeQuery();
        verify(rs, times(2)).next();
        verify(rs).getInt("flightid");
        verify(rs).getString("arrival");
        verify(rs).getInt("price");
        verify(rs).close();
        verify(ps).close();
        verify(conn).close();
    }

    @Test
    public void testCheckFlightForDiscountThrowsException() throws SQLException, IllegalAccessException {
        flightNonDAO = new FlightNonDAOImpl();

        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        when(rs.getInt("flightid")).thenReturn(1);
        when(rs.getString("arrival")).thenReturn("London");
        when(rs.getInt("price")).thenReturn(150);
        doNothing().when(rs).close();
        doNothing().when(ps).close();
        doNothing().when(conn).close();


        Field[] fields = FlightNonDAOImpl.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (fieldName.equals("db")) {
                field.set(flightNonDAO, ds);
            }
        }

        ThrowableAssert.ThrowingCallable code = () -> {
            flightNonDAO.checkFlightForDiscount(1);
        };

        assertThatThrownBy(code).isInstanceOf(DiscountDatabaseException.class);
    }


}
