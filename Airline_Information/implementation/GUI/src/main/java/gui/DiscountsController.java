package gui;

import businessLogic.*;
import dataRecords.FlightData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import persistence.DiscountDatabaseException;
import persistence.FlightPersistenceException;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class DiscountsController implements Initializable {
    @FXML
    private Button backButton;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> locationsList;
    @FXML
    private Label selectedLocation;
    @FXML
    private Button dynamicDiscountButton;
    @FXML
    private TableView<Flight> flightList;
    @FXML
    private TableColumn<Flight, Integer> idColumn;
    @FXML
    private TableColumn<Flight, String> departureColumn;
    @FXML
    private TableColumn<Flight, String> arrivalColumn;
    @FXML
    private TableColumn<Flight, LocalDate> depDateColumn;
    @FXML
    private TableColumn<Flight, LocalDate> arrDateColumn;
    @FXML
    private TableColumn<Flight, Integer> priceColumn;
    @FXML
    private TextField flightSearchField;
    @FXML
    private Button flightDDButton;
    @FXML
    private Label selectedFlight;
    @FXML
    private ComboBox<String> discountModes;
    private String selectedLocationName;
    private String selectedFlightName;
    private int selectedFlightId;
    @FXML
    private Label selectedFlightStatic;
    @FXML
    private ComboBox<Integer> staticDiscountCombo;
    @FXML
    private Button staticDiscountButton;


    private ObservableList<String> locations = FXCollections.observableArrayList();
    private ObservableList<Flight> flights = FXCollections.observableArrayList();
    private final ObservableList<String> comboBoxOptions = FXCollections.observableArrayList(
            "Sun hours",
            "Clouds"
    );


    private final ObservableList<Integer> staticComboBoxOptions = FXCollections.observableArrayList(
            10,
            20,
            30,
            40,
            50,
            60,
            70,
            80,
            90
    );


    private final Supplier<SceneManager> sceneManagerSupplier;

    private final DiscountManager discountManager;

    private final FlightManager fLightManager = new FlightManager();

    public DiscountsController(Supplier<SceneManager> sceneManagerSupplier, DiscountManager discountManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
        this.discountManager = discountManager;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        discountModes.setItems(comboBoxOptions);
        discountModes.setValue("Sun hours");

        staticDiscountCombo.setItems(staticComboBoxOptions);
        staticDiscountCombo.setPromptText("static discount %");

        locations.addAll(getLocations());
        FilteredList<String> filteredLocations = new FilteredList<>(locations);
        locationsList.setItems(filteredLocations);


        searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredLocations.setPredicate(location -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String lowered = newValue.toLowerCase();

            return location.toLowerCase().contains(lowered);
        }));

        locationsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                selectedLocationName = locationsList.getSelectionModel().getSelectedItem();
                selectedLocation.setText(selectedLocationName);
            }
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("id"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("departureAirport"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("arrivalAirport"));
        depDateColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalDate>("departureDate"));
        arrDateColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalDate>("arrivalDate"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("price"));

        flights.addAll(getFlights());
        FilteredList<Flight> filteredFlights = new FilteredList<>(flights);
        flightList.setItems(flights);

        flightSearchField.textProperty().addListener((observable, newValue, oldValue) -> {
            filteredFlights.setPredicate(flight -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowered = newValue.toLowerCase();

                if (String.valueOf(flight.getId()).contains(lowered)) {
                    return true;
                } else if (flight.getDepartureAirport().toLowerCase().contains(lowered)) {
                    return true;
                } else if (flight.getArrivalAirport().toLowerCase().contains(lowered)) {
                    return true;
                } else if (String.valueOf(flight.getPrice()).contains(lowered)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Flight> sortedList = new SortedList<>(filteredFlights);

        sortedList.comparatorProperty().bind(flightList.comparatorProperty());

        flightList.setItems(sortedList);

        flightList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Flight>() {
            @Override
            public void changed(ObservableValue<? extends Flight> observableValue, Flight flightData, Flight t1) {
                selectedFlightName = String.valueOf(flightList.getSelectionModel().getSelectedItem().getId());
                selectedFlightId = flightList.getSelectionModel().getSelectedItem().getId();
                selectedFlight.setText(selectedFlightName);
                selectedFlightStatic.setText(selectedFlightName);
            }
        });
    }
    @FXML
    public void switchBackToMain(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("mainView");

    }

    @FXML
    public void applyDiscountToLocation(ActionEvent e) {
        if (!(selectedLocationName == null)) {
            try {
                this.discountManager.applyDiscountToLocation(selectedLocationName, discountModes.getSelectionModel().getSelectedItem());
                showAlert("Discount applied", "Dynamic discount successfully applied for flights to " + selectedLocationName, Alert.AlertType.INFORMATION);
            } catch (DiscountDatabaseException | DiscountNotAppliedException exception) {
                showAlert("Discount not applied", exception.getMessage(), Alert.AlertType.ERROR);
            }

        } else {
            showAlert("Discount not applied", "No location selected", Alert.AlertType.ERROR);
        }

    }

    @FXML
    public void applyDiscountToFlight(ActionEvent e) {
        if (!(selectedFlightName == null)) {
            try {
                this.discountManager.applyDiscountToFlight(selectedFlightId, discountModes.getSelectionModel().getSelectedItem());
                showAlert("Discount applied", "Dynamic discount successfully applied to flight with id " + selectedFlightId, Alert.AlertType.INFORMATION);
            } catch (DiscountDatabaseException | DiscountNotAppliedException ex) {
//                throw new RuntimeException(ex);
                showAlert("Discount not applied", ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Discount not applied", "No flight selected", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void staticApplyDiscountToFlight(ActionEvent e) {
        if (!(selectedFlightName == null)) {
            try {
                this.discountManager.staticApplyDiscountToFlight(selectedFlightId, discountModes.getSelectionModel().getSelectedItem());
                showAlert("Discount applied", "Dynamic discount successfully applied to flight with id " + selectedFlightId, Alert.AlertType.INFORMATION);
            } catch (DiscountDatabaseException | DiscountNotAppliedException ex) {
//                throw new RuntimeException(ex);
                showAlert("Discount not applied", ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Discount not applied", "No flight selected", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType at) {
        Alert alert = new Alert(at);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.setHeaderText(title);
        alert.showAndWait();

    }

    public List<String> getLocations() {
        try {
            return this.discountManager.getFlightLocations();
        } catch (DiscountDatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Flight> getFlights() {
        List<Flight> flights = new ArrayList<>();
        try {
            for (FlightData f : this.fLightManager.getAllFlights()) {
                flights.add(new Flight(f));
            }

            return flights;
//            return this.fLightManager.getAllFlights();
        } catch (FlightPersistenceException e) {
            throw new RuntimeException();
        }
//        return null;
    }
}
