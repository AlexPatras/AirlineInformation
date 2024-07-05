package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

import businessLogic.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.nio.charset.StandardCharsets;

import dataRecords.StartSalesData;
import dataRecords.ExtrasData;
import dataRecords.FlightData;
import dataRecords.PassengerData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.Match;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import persistence.GeneralPurpose;
//import persistence.GeneralPurposeImpl;
import persistence.PlaneNonDAO;
import persistence.PlaneNonDAOImpl;

public class StartSalesController implements Initializable {
    // private final Supplier<SceneManager> sceneManagerSupplier;
    // private final PlaneManager planeManager;
    @FXML
    private TextField flightIdField;
    @FXML
    private TableView<Flight> flightTable;
    @FXML
    private TableColumn<Flight, Integer> idColumn;
    @FXML
    private TableColumn<Flight, String> departureColumn;
    @FXML
    private TableColumn<Flight, String> arrivalColumn;
    @FXML
    private TableColumn<Flight, LocalTime> depTimeColumn;
    @FXML
    private TableColumn<Flight, LocalTime> arrTimeColumn;
    @FXML
    private TableColumn<Flight, LocalDate> depDateColumn;
    @FXML
    private TableColumn<Flight, LocalDate> arrDateColumn;
    @FXML
    private Button searchFlightButton;
    @FXML
    private Label flightErrorLabel;

    private final Supplier<SceneManager> sceneManagerSupplier;

    String mainError = "";

    public StartSalesController(Supplier<SceneManager> sceneManagerSupplier, StartSalesManager startSalesManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
        this.startSalesManager = startSalesManager;
    }

    private void resetStartSalesView() {

        flightIdField.setText("");
    }

    @FXML
    public void switchToMain(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("mainView");
    }

    @FXML
    private void createStartSales(Event event) {

        String error = "";

        if (flightIdField.getText().isEmpty()) {
            error += "\n" + "Please provide a flight ID.";
        }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        resetStartSalesView();

        idColumn.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("id"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("departureAirport"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("arrivalAirport"));
        depTimeColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalTime>("departureTime"));
        arrTimeColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalTime>("arrivalTime"));
        depDateColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalDate>("departureDate"));
        arrDateColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalDate>("arrivalDate"));

        FlightManager flightManager = new FlightManagerImpl();

        ObservableList<Flight> data = FXCollections.observableArrayList();

        try {
            List<FlightData> list = flightManager.getAllFlights();

            for (FlightData e : list) {
                Flight flight = new Flight(e);
                data.add(flight);
            }
        } catch (FlightBusinessException e) {
            mainError += "Couldn't get all flights.";
        }

        flightTable.setItems(data);

        if (data.isEmpty()) {
            flightTable.setPlaceholder(new Label("No flights available."));
        }

        flightTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        flightTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                TableColumn<Flight, ?> firstColumn = flightTable.getColumns().get(0);
                Object value = firstColumn.getCellData(newSelection);

                flightIdField.setText(value.toString());
            }
        });
    }
}