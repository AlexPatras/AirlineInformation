// package persTest;

// import dataRecords.PlaneData;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import persistence.UserDAO;
// import persistence.UserDAOImpl;

// import javax.sql.DataSource;
// import java.sql.Connection;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.util.ArrayList;

// import static org.assertj.core.api.Assertions.*;
// import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
// import static org.mockito.Mockito.*;

// public class UserDAOImplTest {

//     private UserDAO userDAO;

//     @Test
//     public void testGetAllPlanes() throws SQLException {
//         userDAO = new UserDAOImpl();

//         ArrayList<PlaneData> expectedPlanes = new ArrayList<>();

//         PlaneData planeData1 = mock(PlaneData.class);
//         when(planeData1.id()).thenReturn(1);
//         when(planeData1.model()).thenReturn("Airbus A220");
//         when(planeData1.numberOfSeats()).thenReturn(123);

//         PlaneData planeData2 = mock(PlaneData.class);
//         when(planeData2.id()).thenReturn(2);
//         when(planeData2.model()).thenReturn("Airbus B220");
//         when(planeData2.numberOfSeats()).thenReturn(321);

//         expectedPlanes.add(planeData1);
//         expectedPlanes.add(planeData2);

//         DataSource dataSource = mock(DataSource.class);
//         Connection conn = mock(Connection.class);
//         Statement stm = mock(Statement.class);
//         ResultSet resultSet = mock(ResultSet.class);
//         String sql = "SELECT * FROM plane";

//         when(dataSource.getConnection()).thenReturn(conn);
//         when(conn.createStatement()).thenReturn(stm);
//         when(stm.executeQuery(sql)).thenReturn(resultSet);

//         when(resultSet.next()).thenReturn(true, true, false);
//         when(resultSet.getInt("id")).thenReturn(1, 2);
//         when(resultSet.getString("model")).thenReturn("Airbus A220", "Airbus B220");
//         when(resultSet.getInt("numberofseats")).thenReturn(123, 321);

//         ArrayList<PlaneData> actualPlanes = userDAO.getAllPlanes(dataSource);
// //        userDAO.getAllPlanes(dataSource);

//         assertThat(actualPlanes.size()).isEqualTo(expectedPlanes.size());

//         verify(stm, times(1)).executeQuery(sql);
//         verify(stm).close();
//         verify(conn).close();
//     }

//     @Test
//     public void testStorePlaneFalseValues() {
//         userDAO = new UserDAOImpl();
//         DataSource dataSource = mock(DataSource.class);
//         PlaneData planeData = new PlaneData(43, "Airbus A232", -8);
//         assertThatThrownBy(() -> userDAO.storePlane(planeData, dataSource)).isExactlyInstanceOf(IllegalArgumentException.class);
//     }
// }


