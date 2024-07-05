// package persTest;

// import java.sql.Connection;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;

// import javax.sql.DataSource;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import persistence.PlaneNonDAO;
// import persistence.PlaneNonDAOImpl;
// import persistence.PlanePersistenceException;

// import static org.mockito.Mockito.*;


// import dataRecords.PlaneData;

// public class PersistenceTest {

//     // private DataSource database = DBController.getDataSource("db");

//     // @Test
//     // public void testAddPlaneToDB() {
//     // UserDAO userDAO = new UserDAOImpl();

//     // //TODO mock conn

//     // try {
//     // userDAO.storePlane("Boeing 737", 600);
//     // Connection conn = database.getConnection();
//     // Statement stm = conn.createStatement();
//     // String sql = "SELECT COUNT(id) FROM plane WHERE model='Boeing 737' AND
//     // numberofseats=600";
//     // ResultSet rs = stm.executeQuery(sql);

//     // int numberOfRows = 0;
//     // if (rs.next()) {
//     // numberOfRows = rs.getInt(1);
//     // }

//     // sql = "DELETE FROM plane WHERE model='Boeing 737' AND numberofseats=600";
//     // stm.executeUpdate(sql);

//     // sql = "TRUNCATE TABLE plane RESTART IDENTITY";
//     // stm.executeUpdate(sql);

//     // assertThat(numberOfRows).isEqualTo(1);

//     // rs.close();
//     // stm.close();
//     // conn.close();
//     // } catch (Exception e) {
//     // fail("It shouldn't throw an exception");
//     // }

//     // }

//     @Mock
//     private DataSource dataSource;

//     @Mock
//     private Connection connection;

//     @Mock
//     private Statement statement;

//     private PlaneNonDAO userDAO;

//     @BeforeEach
//     public void setUp() throws SQLException {
//         MockitoAnnotations.openMocks(this);
//         userDAO = new PlaneNonDAOImpl();
//         when(dataSource.getConnection()).thenReturn(connection);
//         when(connection.createStatement()).thenReturn(statement);
//     }

//     @Test
//     public void testStorePlane() throws PlanePersistenceException, SQLException {
//         userDAO = new PlaneNonDAOImpl();
//         PlaneData mockData = mock(PlaneData.class);
//         when(mockData.model()).thenReturn("Boeing 737");
//         when(mockData.numberOfSeats()).thenReturn(600);

//         DataSource mockDataSource = mock(DataSource.class);

//         Connection mockConn = mock(Connection.class);
//         Statement mockStm = mock(Statement.class);

//         when(mockDataSource.getConnection()).thenReturn(mockConn);
//         when(mockConn.createStatement()).thenReturn(mockStm);

//         try {
//             userDAO.storePlane(mockData, mockDataSource);
//         } catch (PlanePersistenceException e) {
//             throw new PlanePersistenceException("Asd", e);
//         }

//         String expectedSql = "INSERT INTO plane (model, numberOfSeats) VALUES ('" + mockData.model() + "', " + mockData.numberOfSeats() + ")";

//         verify(mockStm, times(1)).executeUpdate(expectedSql);

//         verify(mockStm).close();
//         verify(mockConn).close();
//     }
// }
