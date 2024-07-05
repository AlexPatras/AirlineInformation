// package gui;

// import businessLogic.PlaneManager;
// import businessLogic.BusinessLogicAPI;
// import dataRecords.PlaneData;
// import java.io.IOException;
// import java.util.List;

// import javax.sql.DataSource;
// import javafx.scene.control.TableView;
// import javafx.stage.Stage;

// import org.assertj.core.api.SoftAssertions;
// import org.junit.jupiter.api.MethodOrderer;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestMethodOrder;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mock;
// import static org.mockito.Mockito.*;
// import org.testfx.api.FxRobot;
// import org.testfx.framework.junit5.ApplicationExtension;
// import org.testfx.framework.junit5.Start;
// import persistence.DBController;
// import persistence.UserDAO;
// import persistence.UserDAOImpl;

// import javax.sql.DataSource;

// /**
//  *
//  * @author hvd/hom
//  */
// @ExtendWith(ApplicationExtension.class)
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// public class GUIAppTest {

//     // static {
//     // if (Boolean.getBoolean("SERVER")) {
//     // System.setProperty("java.awt.headless", "true");
//     // System.setProperty("testfx.robot", "glass");
//     // System.setProperty("testfx.headless", "true");
//     // System.setProperty("prism.order", "sw");
//     // System.setProperty("prism.text", "t2k");
//     // System.setProperty("glass.platform", "Monocle");
//     // System.setProperty("monocle.platform", "Headless");
//     // }
//     // }
//     @Mock
//     PlaneManager planeManager;
//     UserDAO userDAO;

//     private DataSource db = DBController.getDataSource("db");

//     @Start
//     void start(Stage stage) throws IOException {
//         BusinessLogicAPI businessLogic = () -> planeManager;
//         new GUIApp(businessLogic).init(false).start(stage);
//     }

//     @Test
//     public void testTable() {
//         FxRobot robot = new FxRobot();
//         userDAO = new UserDAOImpl();
//         TableView<PlaneData> planeTable = robot.lookup("#planeTable").queryTableView();
//         List<PlaneData> planes = userDAO.getAllPlanes(db);
//         System.out.println(planeTable.getItems().toString());
//         System.out.println("----");
//         System.out.println(planes);
//         System.out.println(planeTable.getItems());
//         SoftAssertions.assertSoftly(softly -> {
//             // softly.assertThat((List<PlaneData>)planeTable.getItems()).containsAll(planes);
//             softly.assertThat(planeTable.getItems().containsAll(planes)).isTrue();
//             softly.assertThat(planeTable.getItems().size()).isEqualTo(planes.size());
//         });
//     }
// }
