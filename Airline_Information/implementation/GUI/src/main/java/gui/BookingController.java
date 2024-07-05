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

import dataRecords.BookingData;
import dataRecords.FlightData;
import dataRecords.PassengerData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import persistence.BookingPersistenceException;
import persistence.ExtrasPersistenceException;
import persistence.FlightPersistenceException;
import persistence.InputValidationException;
import persistence.PassengerPersistenceException;
//import persistence.GeneralPurposeImpl;
import persistence.PlanePersistenceException;

public class BookingController implements Initializable {
    // private final Supplier<SceneManager> sceneManagerSupplier;
    // private final PlaneManager planeManager;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private ComboBox<Integer> yearField;
    @FXML
    private ComboBox<String> monthField;
    @FXML
    private ComboBox<String> dayField;
    @FXML
    private TextField idPassField;
    @FXML
    private TextField flightIdField;
    @FXML
    private ComboBox<String> nationalityField;
    @FXML
    private ComboBox<String> countryCodeField;
    @FXML
    private Label docNumber;
    @FXML
    private Label result;
    @FXML
    private Button submitButton;
    @FXML
    private HBox extrasContainer;
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
    private TextField searchFlightDeparture;
    @FXML
    private TextField searchFlightArrival;
    @FXML
    private TextField searchFlightDate;
    @FXML
    private Button searchFlightButton;
    @FXML
    private Label flightErrorLabel;

    private final Supplier<SceneManager> sceneManagerSupplier;

    private ObservableList<String> nationalitiesOptions;
    private ObservableList<String> monthsOptions;
    private ObservableList<Integer> yearsOptions;
    private ObservableList<String> daysOptions;
    private ObservableList<String> countryCodeOptions;
    private ObservableList<String> airportIATACodes;
    private ObservableList<String> euNationalities;

    private ArrayList<Integer> extrasIds;

    private String mainError = "";

    private LinkedHashMap<String, String> monthsMap;

    private BookingManager bookingManager;
    private PassengerManager passengerManager;
    private ExtrasManager extrasManager;
    private DiscountManager discountManager;
    private FlightManager flightManager;

    public BookingController(Supplier<SceneManager> sceneManagerSupplier, BookingManager bookingManager,
            PassengerManager passengerManager, ExtrasManager extrasManager, DiscountManager discountManager,
            FlightManager flightManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
        this.bookingManager = bookingManager;
        this.passengerManager = passengerManager;
        this.extrasManager = extrasManager;
        this.discountManager = discountManager;
        this.flightManager = flightManager;
    }

    private void resetBookingView() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneNumberField.setText("");

        yearField.setValue(null);
        yearField.setPromptText("Year");

        monthField.setValue(null);
        monthField.setPromptText("Month");

        dayField.setValue(null);
        dayField.setPromptText("Day");

        idPassField.setText("");
        flightIdField.setText("");

        nationalityField.setValue(null);
        nationalityField.setPromptText("Select");

        countryCodeField.setValue(null);
        countryCodeField.setPromptText("Prefix");

        flightTable.getSelectionModel().clearSelection();

        extrasContainer.getChildren().clear();
    }

    @FXML
    public void switchToMain(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("mainView");
    }

    private Passenger createPassenger() {

        String error = "";

        // Passenger passenger = null;

        if (firstNameField.getText() == "") {
            error += "\n" + "Please provide the first name of the passenger.";
        } else if (!firstNameField.getText().matches("[a-zA-Z\\-]+")) {
            error += "\n" + "Invalid first name. Only letters and hyphens are allowed.";
        } else if (firstNameField.getText().length() > 30) {
            error += "\n" + "Invalid first name. Length can not be greater than 30 characters";
        }

        if (lastNameField.getText().isEmpty()) {
            error += "\n" + "Please provide the last name of the passenger.";
        } else if (!lastNameField.getText().matches("[a-zA-Z\\-]+")) {
            error += "\n" + "Invalid last name. Only letters and hyphens are allowed.";
        } else if (lastNameField.getText().length() > 30) {
            error += "\n" + "Invalid last name. Length can not be greater than 30 characters";
        }

        if (emailField.getText().isEmpty()) {
            error += "\n" + "Please provide the email of the passenger.";
        } else {
            String emailRegex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(emailField.getText());
            if (!matcher.matches()) {
                error += "\n" + "Invalid email address. Please provide a valid email address.";
            }
        }

        if (countryCodeField.getValue() == null) {
            error += "\n" + "Please provide the country code.";
        }

        if (phoneNumberField.getText().isEmpty()) {
            error += "\n" + "Please provide the phone number.";
        } else {
            String phoneNumberRegex = "\\d+";
            Pattern pattern = Pattern.compile(phoneNumberRegex);
            Matcher matcher = pattern.matcher(phoneNumberField.getText());
            if (!matcher.matches()) {
                error += "\n" + "Invalid phone number. Please provide a valid phone number.";
            }
        }

        if (yearField.getValue() == null || monthField.getValue() == null || dayField.getValue() == null) {
            error += "\n" + "Please provide the full date of birth.";
        }

        if (flightIdField.getText().isEmpty()) {
            error += "\n" + "Please provide a flight ID.";
        }

        if (nationalityField.getValue() == null) {
            error += "\n" + "Please provide the nationality.";
        }

        if (idPassField.getText().isEmpty()) {
            error += "\n" + "Please provide the document number.";
        }

        if (error.isEmpty()) {
            try {
                String birthDay = dayField.getValue() + "-" + monthsMap.get(monthField.getValue()) + "-"
                        + yearField.getValue();
                String phoneNumber = countryCodeField.getValue() + phoneNumberField.getText();
                PassengerData passengerData = passengerManager.create(firstNameField.getText(),
                        lastNameField.getText(), emailField.getText(), phoneNumber,
                        nationalityField.getValue(), idPassField.getText(), birthDay);

                Passenger passenger = new Passenger(passengerData);
                return passenger;
            } catch (PassengerPersistenceException e) {
                result.setText(error);
            } catch (InputValidationException e) {
                result.setText(error);
            }
        }
        return null;
    }

    @FXML
    private void createBooking(Event event) {

        Passenger passenger = createPassenger();

        String error = "";

        try {

            int price = 0;

            ArrayList<Integer> selectedExtrasIds = new ArrayList<>();

            for (Node node : extrasContainer.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) node;
                    if (checkBox.isSelected()) {
                        price += extrasManager.getExtrasPrice(Integer.parseInt(checkBox.getId()));
                        selectedExtrasIds.add(Integer.parseInt(checkBox.getId()));
                    }
                }
            }

            BookingData bookingData = new BookingData(passenger.getPassengerData(), price, generateSeat(),
                    Integer.parseInt(flightIdField.getText()));

            Booking booking = new Booking(bookingData);

            if (passengerManager.checkForExistingPassenger(passenger) == 0) {
                passengerManager.create(passenger);
                bookingManager.create(booking);
            } else if (passengerManager
                    .read(passengerManager.checkForExistingPassenger(passenger)) != passenger) {
                passenger.setPassengerId(passengerManager.checkForExistingPassenger(passenger));
                passengerManager.update(passenger
                        .setPassengerId(passengerManager.checkForExistingPassenger(passenger)));
                bookingManager.create(booking);
            } else {
                bookingManager.create(booking);
            }

            for (Node node : extrasContainer.getChildren()) {
                if (node instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) node;
                    if (checkBox.isSelected()) {
                        extrasManager.createBookingExtras(Integer.parseInt(checkBox.getId()));
                    }
                }
            }

            try {
                discountManager.calculatePrice((bookingManager.getInsertedBookingId()),
                        Integer.parseInt(flightIdField.getText()));
            } catch (DiscountNotAppliedException | BookingPersistenceException e) {
                System.out.println("Exception from: " + e.getMessage());
            }

            BookingData newBookingData = bookingManager.read(bookingManager.getInsertedBookingId());

            Booking newBooking = new Booking(newBookingData);

            result.setText("Booking created successfully. Price: " + newBooking.getPrice());

            FlightData ticketFlightData = null;

            try {
                List<FlightData> flights = flightManager.getAllFlights();
                for (FlightData e : flights) {
                    if (e.id() == Integer.parseInt(flightIdField.getText())) {
                        ticketFlightData = e;
                    }
                }
            } catch (FlightPersistenceException e) {
                mainError += '\n' + "Couldn't get flights from DB.";
            }

            try {

                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                String fontPath = "lib/swansea-font/Swansea-q3pd.ttf";

                PDType0Font font = PDType0Font.load(document,
                        new File(fontPath));
                float fontSize = 12;
                contentStream.setFont(font, fontSize);

                String lastName = booking.getPassenger().lastName();
                String firstName = booking.getPassenger().firstName();

                String departure = ticketFlightData.departureAirport();
                String arrival = ticketFlightData.arrivalAirport();

                LocalDate depDate = ticketFlightData.departureDate();
                LocalDate arrDate = ticketFlightData.arrivalDate();

                LocalTime depTime = ticketFlightData.departureTime();
                LocalTime arrTime = ticketFlightData.arrivalTime();

                String gateNumber = ticketFlightData.gateNr();
                int newPrice = newBookingData.price();

                ArrayList<String> extrasDescriptions = new ArrayList<>();

                if (!selectedExtrasIds.isEmpty()) {
                    for (Integer i : selectedExtrasIds) {
                        if (selectedExtrasIds.contains(i)) {
                            if (i == 1) {
                                extrasDescriptions.add("Extra legroom");
                            } else if (i == 2) {
                                extrasDescriptions.add("Food");
                            } else if (i == 3) {
                                extrasDescriptions.add("Extra luggage.");
                            }
                        }
                    }
                }

                float startX = 50;
                float startY = page.getMediaBox().getHeight() - 50;
                float lineHeight = 20;

                contentStream.beginText();
                contentStream.newLineAtOffset(startX, startY);
                contentStream.showText("Name: " + firstName + " " + lastName);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Departure: " + departure);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Arrival: " + arrival);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Departure date: " + depDate);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Departure time: " + depTime);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Arrival date: " + arrDate);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Arrival time: " + arrTime);
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Gate: " + gateNumber);
                if (!extrasDescriptions.isEmpty()) {
                    contentStream.newLineAtOffset(0, -lineHeight);
                    contentStream.showText("Extras: " + extrasDescriptions);
                }
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Price: " + newPrice);
                contentStream.endText();

                contentStream.close();

                File outputFile = new File("Tickets",
                        "Ticket" + " " + booking.getPassenger().lastName() + " "
                                + booking.getPassenger().firstName() + ".pdf");

                document.save(outputFile);
                document.close();

                System.out.println("PDF created successfully.");
            } catch (IOException e) {
                error += '\n' + "Couldn't generate ticket";
            }
        } catch (Exception e) {
            error += "\n" + "Failed to create booking: " + e.getMessage();
            result.setText(error);
        }

        resetBookingView();

    }

    private String generateSeat() throws PlaneGUIException {
        PlaneManager planeManager = new PlaneManager();
        int nrOfSeats = -1;
        int leftRow = -1;
        int rightRow = -1;
        ArrayList<String> allSeats = new ArrayList<>();

        try {
            nrOfSeats = planeManager.getNumberOfSeats(Integer.parseInt(flightIdField.getText()));
        } catch (PlanePersistenceException e) {
            throw new PlaneGUIException("Couldn't get number of seats.");
        }

        if (nrOfSeats % 2 == 0) {
            leftRow = nrOfSeats / 2;
            rightRow = nrOfSeats / 2;
        } else {
            leftRow = nrOfSeats / 2 + 1;
            rightRow = nrOfSeats / 2;
        }

        for (int row = 1; row <= rightRow / 3; row++) {
            for (char seat = 'A'; seat <= 'F'; seat++) {
                String seatLabel = row + " " + seat;
                allSeats.add(seatLabel);
            }
        }

        if (leftRow % 3 != 0) {
            for (int row = leftRow / 3; row <= (leftRow / 3) + (leftRow % 3); row++) {
                for (char seat = 'A'; seat <= 'B'; seat++) {
                    String seatLabel = row + " " + seat;
                    allSeats.add(seatLabel);
                }
            }
        }

        if (rightRow % 3 != 0) {
            for (int row = rightRow / 3; row <= (rightRow / 3) + (rightRow % 3); row++) {
                for (char seat = 'D'; seat <= 'E'; seat++) {
                    String seatLabel = row + " " + seat;
                    allSeats.add(seatLabel);
                }
            }
        }

        ArrayList<String> occupiedSeats = new ArrayList<>();

        try {
            occupiedSeats = planeManager.getOccupiedSeats();
        } catch (PlanePersistenceException e) {
            throw new PlaneGUIException("Couldn't get occupied seats.");
        }

        ArrayList<String> freeSeats = new ArrayList<String>(allSeats);
        freeSeats.removeAll(occupiedSeats);

        Random random = new Random();
        int rand = random.nextInt(freeSeats.size());

        String generatedRandomSeat = freeSeats.get(rand);

        return generatedRandomSeat;

    }

    public void searchFlight(Event event) {

        String flightError = "";

        if (searchFlightDeparture.getText().isEmpty()) {
            flightError = "Departure IATA code can not be empty.";
        } else {
            String regex = ".*[A-Z].*[A-Z].*[A-Z].*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(searchFlightDeparture.getText());
            if (!matcher.matches()) {
                flightError += "\n" + "Invalid IATA code. Please provide a valid IATA code.";
            }
        }

        if (searchFlightArrival.getText().isEmpty()) {
            flightError += "\n" + "Arrival IATA code can not be empty.";
        } else {
            String regex = ".*[A-Z].*[A-Z].*[A-Z].*";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(searchFlightArrival.getText());
            if (!matcher.matches()) {
                flightError += "\n" + "Invalid IATA code. Please provide a valid IATA code.";
            }
        }

        if (searchFlightDate.getText().isEmpty()) {
            flightError += "\n" + "Departure date can not be empty.";
        } else {
            String regex = "\\d{4}-\\d{2}-\\d{2}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(searchFlightDate.getText());
            if (!matcher.matches()) {
                flightError += "\n" + "Invalid departure date. Please provide a valid departure date.";
            }
        }

        if (flightError.isEmpty()) {
            ObservableList<Flight> dataFound = FXCollections.observableArrayList();
            try {
                LocalDate localDate = LocalDate.parse(searchFlightDate.getText());
                Date date = Date.valueOf(localDate);
                List<FlightData> flightsFound = flightManager.getFlight(searchFlightDeparture.getText(),
                        searchFlightArrival.getText(), date);
                for (FlightData e : flightsFound) {
                    Flight flight = new Flight(e);
                    dataFound.add(flight);
                }

            } catch (Exception e) {
                flightError += "\n" + "Couldn't get flights." + searchFlightDeparture.getText()
                        + searchFlightArrival.getText() + searchFlightDate.getText();
                flightErrorLabel.setText(flightError);
                e.printStackTrace();
            }
            flightTable.setItems(dataFound);
            if (dataFound.isEmpty()) {
                flightTable.setPlaceholder(new Label("No flights found."));
            }
        } else {
            flightErrorLabel.setText(flightError);
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        resetBookingView();

        idColumn.setCellValueFactory(new PropertyValueFactory<Flight, Integer>("id"));
        departureColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("departureAirport"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<Flight, String>("arrivalAirport"));
        depTimeColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalTime>("departureTime"));
        arrTimeColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalTime>("arrivalTime"));
        depDateColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalDate>("departureDate"));
        arrDateColumn.setCellValueFactory(new PropertyValueFactory<Flight, LocalDate>("arrivalDate"));

        searchFlightDeparture.setPromptText("Departure");
        searchFlightArrival.setPromptText("Arrival");
        searchFlightDate.setPromptText("Date (YYYY-MM-DD)");

        searchFlightDeparture.setOnMouseClicked(event -> {
            searchFlightDeparture.setText("");
        });

        searchFlightArrival.setOnMouseClicked(event -> {
            searchFlightArrival.setText("");
        });

        searchFlightDate.setOnMouseClicked(event -> {
            searchFlightDate.setText("");
        });

        ObservableList<Flight> data = FXCollections.observableArrayList();

        try {
            List<FlightData> list = flightManager.getAllFlights();

            for (FlightData e : list) {
                Flight flight = new Flight(e);
                data.add(flight);
            }
        } catch (FlightPersistenceException e) {
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

        this.monthsMap = new LinkedHashMap<>();

        this.monthsMap.put("JAN", "01");
        this.monthsMap.put("FEB", "02");
        this.monthsMap.put("MAR", "03");
        this.monthsMap.put("APR", "04");
        this.monthsMap.put("MAY", "05");
        this.monthsMap.put("JUN", "06");
        this.monthsMap.put("JUL", "07");
        this.monthsMap.put("AUG", "08");
        this.monthsMap.put("SEP", "09");
        this.monthsMap.put("OCT", "10");
        this.monthsMap.put("NOV", "11");
        this.monthsMap.put("DEC", "12");

        nationalitiesOptions = FXCollections.observableArrayList();
        yearsOptions = FXCollections.observableArrayList();
        monthsOptions = FXCollections.observableArrayList();
        daysOptions = FXCollections.observableArrayList();
        countryCodeOptions = FXCollections.observableArrayList();
        airportIATACodes = FXCollections.observableArrayList();
        euNationalities = FXCollections.observableArrayList();

        try {
            InputStream inputStream = BookingController.class.getResourceAsStream("/gui/nationalities.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                nationalitiesOptions.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nationalitiesOptions.sort(String::compareTo);

        nationalityField.setItems(nationalitiesOptions);

        try {
            InputStream inputStream = BookingController.class.getResourceAsStream("/gui/years.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                int value = Integer.parseInt(line);
                yearsOptions.add(value);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        yearField.setItems(yearsOptions);

        monthField.getItems().addAll(monthsMap.keySet());

        try {
            InputStream inputStream = BookingController.class.getResourceAsStream("/gui/phoneNumberCodes.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                countryCodeOptions.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        countryCodeOptions.sort(String::compareTo);

        countryCodeField.setItems(countryCodeOptions);

        final String[] selectedMonth = { "0" };

        monthField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedMonth[0] = monthsMap.get(newValue);
                    }
                });
        final int[] selectedYear = { 0 };

        yearField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedYear[0] = newValue;
                        daysOptions.clear();

                        try {
                            InputStream inputStream = BookingController.class.getResourceAsStream("/gui/days.txt");
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                                    StandardCharsets.UTF_8);
                            BufferedReader reader = new BufferedReader(inputStreamReader);

                            String line;
                            while ((line = reader.readLine()) != null) {
                                daysOptions.add(line);
                            }
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int monthNumber = Integer.parseInt(selectedMonth[0]);
                        if (((monthNumber % 2 == 0 && monthNumber < 7)
                                || (monthNumber % 2 == 1 && monthNumber > 8)) && monthNumber != 2) {
                            daysOptions.remove("31");
                            dayField.setItems(daysOptions);
                        } else if (monthNumber == 2) {
                            daysOptions.remove("31");
                            daysOptions.remove("30");
                            if (selectedYear[0] % 4 != 0) {
                                daysOptions.remove("29");
                            }
                            dayField.setItems(daysOptions);
                        } else {
                            dayField.setItems(daysOptions);
                        }
                    }
                });

        monthField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                daysOptions.clear();

                try {
                    InputStream inputStream = BookingController.class.getResourceAsStream("/gui/days.txt");
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        daysOptions.add(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int monthNumber = Integer.parseInt(monthsMap.get(newValue.toUpperCase()));
                if (((monthNumber % 2 == 0 && monthNumber < 7)
                        || (monthNumber % 2 == 1 && monthNumber > 8)) && monthNumber != 2) {
                    daysOptions.remove("31");
                    dayField.setItems(daysOptions);
                } else if (monthNumber == 2) {
                    daysOptions.remove("31");
                    daysOptions.remove("30");
                    if (selectedYear[0] % 4 != 0) {
                        daysOptions.remove("29");
                    }
                    dayField.setItems(daysOptions);
                } else {
                    dayField.setItems(daysOptions);
                }
            }
        });

        dayField.setItems(daysOptions);

        try {
            InputStream inputStream = BookingController.class.getResourceAsStream("/gui/EU_EEA_IATACodes.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                airportIATACodes.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream = BookingController.class.getResourceAsStream("/gui/EU_EEA_Nationalities.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                euNationalities.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nationalityField.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        try {
                            if (airportIATACodes
                                    .contains(flightManager
                                            .getArrivalIATACode(Integer.parseInt(flightIdField.getText())))
                                    && euNationalities.contains(nationalityField.getValue())) {
                                docNumber.setText("ID Number");
                            } else {
                                docNumber.setText("Passport number");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        flightIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                extrasContainer.getChildren().clear();
                try {
                    extrasIds = new ArrayList<>();
                    extrasIds = extrasManager.getExtrasIds(Integer.parseInt(flightIdField.getText()));
                    for (int i : extrasIds) {
                        Extras extrasData = extrasManager.getExtras(i);
                        CheckBox checkBox = new CheckBox(extrasData.getDescription());
                        checkBox.setId("" + i);
                        extrasContainer.getChildren().add(checkBox);
                    }
                } catch (ExtrasPersistenceException e) {
                    mainError += '\n' + "Couldn't display extras.";
                }
            }
        });
        result.setText(mainError);
    }
}
