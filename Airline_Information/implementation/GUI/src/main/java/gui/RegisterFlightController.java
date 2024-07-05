/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gui;

import businessLogic.Flight;
import businessLogic.FlightBusinessException;
import businessLogic.FlightManager;
import dataRecords.AirportValidationException;
import dataRecords.DateTimeValidationException;
import dataRecords.FlightData;
import dataRecords.FlightDetailsValidationException;
import dataRecords.PlaneData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import persistence.DBController;
import persistence.FlightPersistenceException;

/**
 * FXML Controller class
 *
 * @author patrasalexandru
 */
public class RegisterFlightController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox<String> depAirportField;
    @FXML
    private ComboBox<String> arrAirportField;
    @FXML
    private DatePicker depDate;
    @FXML
    private DatePicker arrDate;
    @FXML
    private Spinner<Integer> depHour;
    @FXML
    private Spinner<Integer> depMinute;
    @FXML
    private Spinner<Integer> arrHour;
    @FXML
    private Spinner<Integer> arrMinute;
    @FXML
    private TextField gateNumber;
    @FXML
    private ComboBox<String> modelField;
    @FXML
    private TextField nrOfSeats;
    @FXML
    private ComboBox<String> currency;
    @FXML
    private TextField price;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;
    @FXML
    private Label result;
    @FXML
    private Label errors;
    @FXML
    private ComboBox<String> depTimeZoneField;
    @FXML
    private ComboBox<String> arrTimeZoneField;

    private final Supplier<SceneManager> sceneManagerSupplier;
    private final FlightManager flightManager;
    private DataSource db = DBController.getDataSource("db");

    RegisterFlightController(java.util.function.Supplier<gui.SceneManager> sceneManagerSupplier,
            FlightManager flightManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
        this.flightManager = flightManager;
    }

    public void initialize(URL url, ResourceBundle rb) {
        setUpDate();
        setUpTime();
        retrieveIATACode();
        retrieveModel();
        setupModelAndSeatsFields();
        addCurrency();
        TimeZones();
    }

    private void setUpTime() {

        SpinnerValueFactory<Integer> depHourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        depHourFactory.setWrapAround(true);
        depHour.setValueFactory(depHourFactory);

        SpinnerValueFactory<Integer> depMinuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        depMinuteFactory.setWrapAround(true);
        depMinute.setValueFactory(depMinuteFactory);

        SpinnerValueFactory<Integer> arrHourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        arrHourFactory.setWrapAround(true);
        arrHour.setValueFactory(arrHourFactory);

        SpinnerValueFactory<Integer> arrMinuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);
        arrMinuteFactory.setWrapAround(true);
        arrMinute.setValueFactory(arrMinuteFactory);
    }

    private void TimeZones() {
        List<String> timeZone = ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .collect(Collectors.toList());
        depTimeZoneField.getItems().addAll(timeZone);
        arrTimeZoneField.getItems().addAll(timeZone);

    }

    private void setUpDate() {
        depDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        arrDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
    }

    private void retrieveIATACode() {
        try {

            List<String> iataCodes = flightManager.getIATACode();
            depAirportField.getItems().setAll(iataCodes);
            arrAirportField.getItems().setAll(iataCodes);
        } catch (FlightPersistenceException ex) {
            System.out.println("Couldn't get IATA codes");

        }

    }

    private void retrieveModel() {
        try {

            ArrayList<String> modelAndSeats = flightManager.getPlaneDetails();

            ArrayList<String> models = new ArrayList<>();
            for (String modelAndSeat : modelAndSeats) {
                String model = modelAndSeat.split(" - ")[0];
                models.add(model);
            }
            modelField.getItems().setAll(models);
        } catch (FlightPersistenceException ex) {
            System.out.println("Error: couldn't get model and number of seats");

        }
    }

    private void setupModelAndSeatsFields() {
        nrOfSeats.setEditable(false);
        modelField.setOnAction(e -> {
            String selectModel = modelField.getValue();
            if (selectModel != null) {
                try {
                    ArrayList<String> modelAndSeats = flightManager.getPlaneDetails();
                    for (String modelAndSeat : modelAndSeats) {
                        String[] parts = modelAndSeat.split(" - ");
                        if (selectModel.equals(parts[0])) {
                            nrOfSeats.setText(parts[1]);
                            break;
                        }
                    }

                } catch (FlightPersistenceException ex) {
                    System.out.println("Error: couldn't get model and number of seats");

                }
            }
        });
    }

    private void addCurrency() {
        List<String> currencies = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/gui/currencies.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                currencies.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        currency.getItems().setAll(currencies);
    }

    @FXML
    private void switchToMain(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("mainView");
    }

    @FXML
    private void resetView() {
        depAirportField.setValue(null);
        arrAirportField.setValue(null);
        depDate.setValue(null);
        arrDate.setValue(null);
        depHour.getValueFactory().setValue(0);
        depMinute.getValueFactory().setValue(0);
        arrHour.getValueFactory().setValue(0);
        arrMinute.getValueFactory().setValue(0);
        gateNumber.clear();
        modelField.setValue(null);
        nrOfSeats.clear();
        currency.setValue(null);
        price.clear();
        errors.setText("");
        depTimeZoneField.setValue(null);
        arrTimeZoneField.setValue(null);

    }

    @FXML
    private void registerFlight(ActionEvent e) throws FlightGUIException, FlightBusinessException, FlightDetailsValidationException {
        String error = "";
        List<String> exceptionMessages = new ArrayList<>();

        if (depAirportField.getValue() == null) {
            error += "\n" + "The departure airport must be selected!";
        }

        if (arrAirportField.getValue() == null) {
            error += "\n" + "The arrival airport must be selected!";
        }

        if (gateNumber.getText().isEmpty()) {
            error += "\n" + "The gate number can't be empty!";
        }

        if (depDate.getValue() == null) {
            error += "\n" + "The departure date can't be empty!";
        }

        if (arrDate.getValue() == null) {
            error += "\n" + "The arrival date can't be empty!:";
        }

        if (depHour.getValue() == 0 && depMinute.getValue() == 0) {
            error += "\n" + "The departure time must be chosen!";

        }

        if (arrHour.getValue() == 0 && arrMinute.getValue() == 0) {
            error += "\n" + "The arrival time must be chosen!";
        }

        if (modelField.getValue() == null) {
            error += "\n" + "The model can't be empty!";
        }

        if (price.getText().isEmpty()) {
            error += "\n" + "The price can't be empty!";
        }

        if (currency.getValue() == null) {
            error += "\n" + "The currency must be selected!";
        }

        if (error.isEmpty()) {

            try {
                PlaneData planeData = new PlaneData(0, modelField.getValue(), Integer.parseInt(nrOfSeats.getText()));
                String depTimeZone = depTimeZoneField.getValue();
                String arrTimeZone = arrTimeZoneField.getValue();

                LocalTime LocalDepTime = LocalTime.of(depHour.getValue(), depMinute.getValue());
                ZonedDateTime depZonedDateTime = ZonedDateTime.of(depDate.getValue(), LocalDepTime, ZoneId.systemDefault());
                ZonedDateTime depTimeZoneDateTime = depZonedDateTime.withZoneSameInstant(ZoneId.of(depTimeZone));
                LocalTime departureTime = depTimeZoneDateTime.toLocalTime();

                LocalTime LocalArrTime = LocalTime.of(arrHour.getValue(), arrMinute.getValue());
                ZonedDateTime arrZonedDateTime = ZonedDateTime.of(arrDate.getValue(), LocalArrTime, ZoneId.systemDefault());
                ZonedDateTime arrTimeZoneDateTime = arrZonedDateTime.withZoneSameInstant(ZoneId.of(arrTimeZone));
                LocalTime arrivalTime = arrTimeZoneDateTime.toLocalTime();

                FlightData flightData = new FlightData(0, depAirportField.getValue(), arrAirportField.getValue(), depDate.getValue(), arrDate.getValue(), departureTime, arrivalTime, Integer.parseInt(price.getText()), currency.getValue(),
                        gateNumber.getText(), planeData);
                Flight flight = new Flight(flightData);

                if (flightManager.CheckRegisterFlights(flightData, db) == true) {
                    result.setText("The flight is already registered");

                } else {
                    flightManager.create(flight);
                    result.setText("The flight has been registered!");

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(event -> {
                        result.setText("");
                        resetView();

                    });
                    pause.play();
                }

            } catch (FlightPersistenceException ex) {
                errors.setText("Error: " + ex.getMessage());

            } catch (FlightDetailsValidationException ex) {
                errors.setText("Error: " + ex.getMessage());

            } catch (DateTimeValidationException ex) {
                errors.setText("Error: " + ex.getMessage());

            } catch (AirportValidationException ex) {
                errors.setText("Error: " + ex.getMessage());
            }

        } else {
            errors.setText(error);
        }
    }
}
