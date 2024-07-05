package gui;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.function.Supplier;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author nl.fontys.prj2.13
 */
public class ManagementDashboardController implements Initializable {

    @FXML
    private Button returnButton;
    @FXML
    private TableView<?> flightTable;
    @FXML
    private TableColumn<?, Integer> flightNumberColumn;
    @FXML
    private TableColumn<?, String> routeColumn;
    @FXML
    private AreaChart<String, Integer> soldTicketsGraph;

    private final Supplier<SceneManager> sceneManagerSupplier;

    /**
     * Constructor Method for ManagementDashboadController
     *
     * @param sceneManagerSupplier Supplier attribute shared with controller
     */
    public ManagementDashboardController(Supplier<SceneManager> sceneManagerSupplier) {
        this.sceneManagerSupplier = sceneManagerSupplier;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    /**
     * Method triggered when press returnButton in View
     *
     * @param event
     */
    @FXML
    private void returnToPreviousPage(ActionEvent event) {
        sceneManagerSupplier.get().changeScene("mainView");
    }

    @FXML
    private void openUserManager(ActionEvent event) {
        sceneManagerSupplier.get().changeScene("manageAccountsView");
    }
    
    private void updateSoldTicketsGraph() {
        // TODO
    }
}
