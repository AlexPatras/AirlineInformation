package gui;

import businessLogic.*;
import dataRecords.StartSalesData;
import dataRecords.BookingData;
import dataRecords.FlightData;
import dataRecords.PassengerData;
import dataRecords.PlaneData;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import persistence.GeneralPurpose;
//import persistence.GeneralPurposeImpl;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable {

    @FXML
    private Button createBookingButton;
    @FXML
    private Button mBoardButton;
    @FXML
    private Button registerFlightButton;
    @FXML
    private Button addPlaneButton;
    @FXML
    private Button addAirportButton;
    @FXML
    private Button staticDiscountButton;
    @FXML
    private Button dynamicDiscountButton;
    @FXML
    private Button StartSalesButton;

    @FXML
    private Button discountsButton;
    @FXML
    private TextField searchField;
    @FXML
    private Label selectedFlight;
    @FXML
    private Label priceOfFlight;

    @FXML
    private Label DynamicDiscountMessage;
    @FXML
    private TableView<FlightData> flightTable;
    @FXML
    private TableColumn<FlightData, Integer> idColumn;

    // @FXML
    // private TextField firstNameField;
    // @FXML
    // private TextField lastNameField;
    // @FXML
    // private TextField emailField;
    // @FXML
    // private TextField flightIdField;
    // @FXML
    // private Label result;
    // @FXML
    // private Button submitButton;
    // TODO: change String to Airport when it is created
    @FXML
    private TableColumn<FlightData, String> departureColumn;
    // TODO: change String to Airport when it is created
    @FXML
    private TableColumn<FlightData, String> arrivalColumn;

    @FXML
    private TableColumn<FlightData, LocalDate> dDateColumn;
    @FXML
    private TableColumn<FlightData, LocalDate> aDateColumn;
    @FXML
    private TableColumn<FlightData, LocalTime> dTimeColumn;
    @FXML
    private TableColumn<FlightData, LocalTime> aTimeColumn;
    @FXML
    private TableColumn<FlightData, PlaneData> planeColumn;

    // TODO: create method to retrieve flights from db, this list is just to test
    PlaneData planeData1 = new PlaneData(1, "Model1", 150);
    PlaneData planeData2 = new PlaneData(2, "Model2", 200);
    PlaneData planeData3 = new PlaneData(3, "Model3", 100);

    private final List<FlightData> flightListForTesting = List.of(new FlightData(1, "Vienna", "Amsterdam",
            LocalDate.now(), LocalDate.now(), LocalTime.now(), LocalTime.now(), 200, "Euro", "", planeData1),
            new FlightData(2, "Sofia", "Berlin", LocalDate.now(), LocalDate.now(), LocalTime.now(), LocalTime.now(),
                    150, "Euro", "2", planeData2),
            new FlightData(3, "Mumbai", "Washington DC", LocalDate.now(), LocalDate.now(), LocalTime.now(),
                    LocalTime.now(),
                    330, "Euro", "3", planeData3));

    private final Supplier<SceneManager> sceneManagerSupplier;

    private ObservableList<FlightData> flightList = FXCollections.observableArrayList();
    private String selected;
    // TODO: change selectedArrAirport to airport when created;
    private String selectedArrAirport;
    private int selectedId;
    private int flightPrice;

    private final FlightManager flightManager;

    public MainController(java.util.function.Supplier<gui.SceneManager> sceneManagerSupplier,
            FlightManager flightManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
        this.flightManager = flightManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // idColumn.setCellValueFactory(new PropertyValueFactory<FlightData,
        // Integer>("id"));
        // departureColumn.setCellValueFactory(new PropertyValueFactory<FlightData,
        // String>("departureAirport"));
        // arrivalColumn.setCellValueFactory(new PropertyValueFactory<FlightData,
        // String>("arrivalAirport"));
        // dDateColumn.setCellValueFactory(new PropertyValueFactory<FlightData,
        // LocalDate>("departureDate"));
        // aDateColumn.setCellValueFactory(new PropertyValueFactory<FlightData,
        // LocalDate>("arrivalDate"));
        // dTimeColumn.setCellValueFactory(new PropertyValueFactory<FlightData,
        // LocalTime>("departureTime"));
        // aTimeColumn.setCellValueFactory(new PropertyValueFactory<FlightData,
        // LocalTime>("arrivalTime"));

        // flightList.addAll(flightListForTesting);
        // flightTable.setItems(flightList);

        // FilteredList<FlightData> filteredList = new FilteredList<>(flightList, b ->
        // true);

        // searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        // filteredList.setPredicate(flight -> {
        // if (newValue == null) {
        // return true;
        // }
        // String lowered = newValue.toLowerCase();

        // if (flight.getDepartureAirport().toLowerCase().contains(lowered)) {
        // return true;
        // } else if (flight.getArrivalAirport().toLowerCase().contains(lowered)) {
        // return true;
        // } else if (String.valueOf(flight.getPrice()).toLowerCase().contains(lowered))
        // {
        // return true;
        // } else if (String.valueOf(flight.getId()).toLowerCase().contains(lowered)) {
        // return true;
        // } else {
        // return false;
        // }
        // });
        // });
        // SortedList<FlightData> sortedList = new SortedList<>(filteredList);

        // sortedList.comparatorProperty().bind(flightTable.comparatorProperty());

        // flightTable.setItems(sortedList);

        // flightTable.getSelectionModel().selectedItemProperty().addListener(new
        // ChangeListener<FlightData>() {
        // @Override
        // public void changed(ObservableValue<? extends FlightData> observableValue,
        // FlightData flightData,
        // FlightData t1) {
        // selected = flightTable.getSelectionModel().getSelectedItem().toString();
        // selectedId = flightTable.getSelectionModel().getSelectedItem().getId();
        // selectedArrAirport =
        // flightTable.getSelectionModel().getSelectedItem().getArrivalAirport();
        // DynamicDiscountMessage.setText("");

        // selectedFlight.setText(selected);
        // }
        // });
    }

    // private void resetBookingView() {
    // firstNameField.setText("");
    // lastNameField.setText("");
    // emailField.setText("");
    // flightIdField.setText("");
    // }
    // @FXML
    // private void createBooking(Event event) {
    // BookingManager bookingManager = new BookingManagerImpl();
    // PassengerManager passengerManager = new PassengerManagerImpl();
    // String error = "";
    // if (firstNameField.getText() == "") {
    // error += "\n" + "Please provide the first name of the passenger.";
    // } else if (!firstNameField.getText().matches("[a-zA-Z\\-]+")) {
    // error += "\n" + "Invalid first name. Only letters and hyphens are allowed.";
    // } else if (firstNameField.getText().length() > 30) {
    // error += "\n" + "Invalid first name. Length can not be greater than 30
    // characters";
    // }
    // if (lastNameField.getText().isEmpty()) {
    // error += "\n" + "Please provide the last name of the passenger.";
    // } else if (!lastNameField.getText().matches("[a-zA-Z\\-]+")) {
    // error += "\n" + "Invalid last name. Only letters and hyphens are allowed.";
    // } else if (lastNameField.getText().length() > 30) {
    // error += "\n" + "Invalid last name. Length can not be greater than 30
    // characters";
    // }
    // if (emailField.getText().isEmpty()) {
    // error += "\n" + "Please provide the email of the passenger.";
    // } else {
    // String emailRegex = "^(.+)@(.+)$";
    // Pattern pattern = Pattern.compile(emailRegex);
    // Matcher matcher = pattern.matcher(emailRegex);
    // if (!matcher.matches()) {
    // error += "\n" + "Invalid email address. Please provide a valid email
    // address.";
    // }
    // }
    // if (error.isEmpty()) {
    // try {
    // // PassengerData passengerData = new PassengerData(firstNameField.getText(),
    // // lastNameField.getText(),
    // // emailField.getText());
    // GeneralPurpose createdPassengerObject = new GeneralPurposeImpl();
    // PassengerData passengerData =
    // createdPassengerObject.create(firstNameField.getText(),
    // lastNameField.getText(), emailField.getText());
    // BookingData bookingData = new BookingData(passengerData, 100, generateSeat(),
    // Integer.parseInt(flightIdField.getText()));
    // passengerManager.create(passengerData);
    // bookingManager.create(bookingData);
    // result.setText("Booking created successfully.");
    // } catch (Exception e) {
    // error += "\n" + "Failed to create booking" + e.getMessage();
    // result.setText(error);
    // }
    // } else {
    // result.setText(error);
    // }
    // resetBookingView();
    // }
    // private String generateSeat() throws SQLException {
    // PlaneManager planeManager = new PlaneManager();
    // int nrOfSeats = -1;
    // int leftRow = -1;
    // int rightRow = -1;
    // ArrayList<String> allSeats = new ArrayList<>();
    // // nrOfSeats =
    // // userDAO.getNumberOfSeats(Integer.parseInt(flightIdField.getText()));
    // nrOfSeats =
    // planeManager.getNumberOfSeats(Integer.parseInt(flightIdField.getText()));
    // if (nrOfSeats % 2 == 0) {
    // leftRow = nrOfSeats / 2;
    // rightRow = nrOfSeats / 2;
    // } else {
    // leftRow = nrOfSeats / 2 + 1;
    // rightRow = nrOfSeats / 2;
    // }
    // for (int row = 1; row <= rightRow / 3; row++) {
    // for (char seat = 'A'; seat <= 'F'; seat++) {
    // String seatLabel = row + " " + seat;
    // allSeats.add(seatLabel);
    // }
    // }
    // if (leftRow % 3 != 0) {
    // for (int row = leftRow / 3; row <= (leftRow / 3) + (leftRow % 3); row++) {
    // for (char seat = 'A'; seat <= 'B'; seat++) {
    // String seatLabel = row + " " + seat;
    // allSeats.add(seatLabel);
    // }
    // }
    // }
    // if (rightRow % 3 != 0) {
    // for (int row = rightRow / 3; row <= (rightRow / 3) + (rightRow % 3); row++) {
    // for (char seat = 'D'; seat <= 'E'; seat++) {
    // String seatLabel = row + " " + seat;
    // allSeats.add(seatLabel);
    // }
    // }
    // }
    // GeneralPurpose databaseSeats = new GeneralPurposeImpl();
    // ArrayList<String> occupiedSeats = new ArrayList<>();
    // occupiedSeats = databaseSeats.getOccupiedSeats();
    // ArrayList<String> freeSeats = new ArrayList<String>(allSeats);
    // freeSeats.removeAll(occupiedSeats);
    // Random random = new Random();
    // int rand = random.nextInt(freeSeats.size());
    // String generatedRandomSeat = freeSeats.get(rand);
    // return generatedRandomSeat;
    // }
    @FXML
    public void switchToCreateBooking(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("bookingView");
    }

    public void switchToStartSales(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("startSalesView");
    }

    @FXML
    public void switchToMBoard(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("managementDashboardView");
    }

    @FXML
    public void switchToRegisterFlight(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("registerFlightView");

    }

    @FXML
    public void switchToAddPlane(ActionEvent e) {

        sceneManagerSupplier.get().changeScene("planeView");

    }

    @FXML
    public void switchToAddAirport(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("airportView");
    }

    @FXML
    public void switchToDiscounts(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("discountsView");

    }

    // no need for this method here, it is discounts have new view now
    // @FXML
    // public void applyDynamicDiscount(ActionEvent e) {
    // if (!(selected == null)) {
    // DynamicDiscountMessage.setTextFill(Color.GREEN);
    // DynamicDiscountMessage.setText("Discount applied");
    // DiscountManager dynamicDiscountManager = new DiscountManagerImpl();
    // try {
    // dynamicDiscountManager.update(selectedId, selectedArrAirport);
    // } catch (Exception exception) {
    // DynamicDiscountMessage.setTextFill(Color.RED);
    // DynamicDiscountMessage.setText(exception.getMessage());
    // }
    //
    //
    // } else {
    // DynamicDiscountMessage.setTextFill(Color.RED);
    // DynamicDiscountMessage.setText("No flight selected");
    // }
    // }
}
