package gui;

import businessLogic.Plane;
import businessLogic.PlaneManager;
import dataRecords.PlaneData;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import persistence.DBController;
import persistence.PlaneNonDAO;
import persistence.PlaneNonDAOImpl;
import persistence.PlanePersistenceException;

/**
 * FXML Controller class
 *
 * @author nl.fontys.prj2.13
 */
public class PlaneController implements Initializable {

    @FXML
    private TextField numberOfSeatsTextField;
    @FXML
    private TextField model;
    @FXML
    private TableView<PlaneData> planeTable;
    @FXML
    private TableColumn<PlaneData, String> modelColumn;
    @FXML
    private TableColumn<PlaneData, Integer> numberOfSeatsColumn;
    @FXML
    private TableColumn<PlaneData, Integer> idColumn;
    @FXML
    private Button submitButton;
    @FXML
    private Label result;

    private final Supplier<SceneManager> sceneManagerSupplier;
    private final PlaneManager planeManager;

    // PlaneStorageServiceImpl planeList = new PlaneStorageServiceImpl();
    // private int i = 1;
    /**
     * Constructor
     *
     * @param sceneManagerSupplier
     * @param planeManager
     */
    PlaneController(java.util.function.Supplier<gui.SceneManager> sceneManagerSupplier, PlaneManager planeManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
        this.planeManager = planeManager;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        modelColumn.setCellValueFactory(new PropertyValueFactory<PlaneData, String>("model"));
        numberOfSeatsColumn.setCellValueFactory(new PropertyValueFactory<PlaneData, Integer>("numberOfSeats"));
        idColumn.setCellValueFactory(new PropertyValueFactory<PlaneData, Integer>("id"));
        model.setEditable(true);

        try {
            retrievePlane();
        } catch (PlanePersistenceException e) {
            result.setText("Failed to retrieve plane " + e.getMessage());
        }
        resetView();
    }

    private void resetView() {
        numberOfSeatsTextField.setText("");
        model.setText("");
    }

    /**
     * Triggers when pressed submit button in view
     *
     * @param event
     */

    private DataSource db = DBController.getDataSource("db");

    @FXML
    private void storePlane(Event event) {

        String error = "";

        if (!numberOfSeatsTextField.getText().matches("[0-9]+")) {
            error = "Please use only numbers for number of seats\n";
        } else if (numberOfSeatsTextField.getText().isEmpty()) {
            error = "Please enter a number of seats";
        } else if (Integer.parseInt(numberOfSeatsTextField.getText()) <= 9999
                || Integer.parseInt(numberOfSeatsTextField.getText()) > 0) {
            error = "Please enter a reasonable number of seats";
        }
        if (model.getText().isEmpty()) {
            if (!error.isEmpty()) {
                error += "\n";
            }
            error += "This field can't be let empty!";

        }
        if (error.isEmpty()) {

            try {
                PlaneNonDAO userDAO = new PlaneNonDAOImpl();
                PlaneData planeData = new PlaneData(1, model.getText(),
                        Integer.parseInt(numberOfSeatsTextField.getText()));
                userDAO.storePlane(planeData, db);

                retrievePlane();

                result.setText("Plane added successfully");

            } catch (PlanePersistenceException e) {
                error += "Failed to add plane: " + e.getMessage() + "\n";
                result.setText(error);
            } 
        } else {
            result.setText(error);
        }
        resetView();
    }

    private void retrievePlane() throws PlanePersistenceException {
        try {
            PlaneNonDAO userDAO = new PlaneNonDAOImpl();
            ArrayList<PlaneData> planes = userDAO.getAllPlanes(db);

            ArrayList<String> planesList = new ArrayList<>();
            for (PlaneData plane : planes) {
                planesList.add(plane.toString());
            }
            planeTable.getItems().setAll(planes);
        } catch (PlanePersistenceException e) {
            throw new PlanePersistenceException("Couldn't get plane", e);
        }
    }

}
