package gui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javax.sql.DataSource;

import businessLogic.AirportManager;
import dataRecords.AirportData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import persistence.AirportDAO;
import persistence.AirportDAOImpl;
import persistence.DBController;

public class AirportController implements Initializable {

    @FXML
    private TextField iataCode;

    @FXML
    private TextField city;

    @FXML
    private Button submit;

    @FXML
    private Label result;

    @FXML
    private ComboBox countries;

    @FXML
    private TableView<AirportData> table;

    @FXML
    private TableColumn<AirportData, String> iataColumn;
    
    @FXML
    private TableColumn<AirportData, String> cityColumn;

    @FXML
    private TableColumn<AirportData, String> countryColumn;
    
    ObservableList<String> countryOptions;
    ObservableList<AirportData> dataList = FXCollections.observableArrayList();
    
    private final Supplier<SceneManager> sceneManagerSupplier;
    AirportController(java.util.function.Supplier<gui.SceneManager> sceneManagerSupplier, AirportManager airportManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iataColumn.setCellValueFactory(cellData -> {
            StringProperty property = new SimpleStringProperty(cellData.getValue().iata());
            return property;
        });

        cityColumn.setCellValueFactory(cellData -> {
            StringProperty property = new SimpleStringProperty(cellData.getValue().city());
            return property;
        });

        countryColumn.setCellValueFactory(cellData -> {
            StringProperty property = new SimpleStringProperty(cellData.getValue().country());
            return property;
        });

        countryOptions = FXCollections.observableArrayList();

        // Reading the countries from the countries.txt file
        try {
            InputStream inputStream = AirportController.class.getResourceAsStream("/gui/countries.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                countryOptions.add(line);
            }
            reader.close();

            // Adding the airports from the database to the table
            AirportManager airportManager = new businessLogic.AirportManagerImpl();
            dataList.addAll(airportManager.getAllAirports());

            table.setItems(dataList);


        } catch (Exception e) {
            e.printStackTrace();
        }

        countryOptions.sort(String::compareTo);

        countries.setItems(countryOptions);
    }

    private void resetView() {
        iataCode.setText("");
        city.setText("");
        countries.setValue(0);
    }

    private DataSource db = DBController.getDataSource("db");

    // Method to store the airport in the database
    @FXML
    private void storeAirport(Event event) {

        if (iataCode.getText().isEmpty() || city.getText().isEmpty() || countries.getValue() == null) { // checking if all fields are filled in
            result.setText("Please fill in all fields");
        } else if (iataCode.getText().length() != 3) { // checking if the IATA-Code has a length of 3 characters
            result.setText("The IATA-Code must have a exact length of 3 characters");
        } else {
            try {
                AirportManager airportManager = new businessLogic.AirportManagerImpl();


                // Capitalizing the first letter of the city
                String cityCapitalized = city.getText().substring(0, 1).toUpperCase() + city.getText().substring(1).toLowerCase();
                
                // Store the airport in the database
                AirportData airportData = new AirportData(iataCode.getText().toUpperCase(), cityCapitalized, countries.getValue().toString());
                airportManager.add(airportData);
                dataList.add(airportData);

                resetView();
                result.setText("Airport added: " + airportData.iata() + ": " + airportData.city() + ", " + airportData.country());
                table.setItems(dataList);

            } catch (Exception e) {

                if (e.getMessage().contains("duplicate key")) {
                    resetView();
                    result.setText("Airport with the given IATA-Code already exists.");
                } else if (e.getMessage().contains("IATA Number")) { 
                    resetView();
                    result.setText("The IATA-Code cannot contain numbers.");
                } else if (e.getMessage().contains("City Number")) {
                    resetView();
                    result.setText("The City cannot contain numbers.");
                } else if (e.getMessage().contains("Country Number")) {
                    resetView();
                    result.setText("The Country cannot contain numbers.");
                } else {
                    e.printStackTrace();
                    result.setText("Something went wrong");
                }

            }
        }
    }


    // Method to go back to the main view
    @FXML
    public void backToMain(ActionEvent e) {
        sceneManagerSupplier.get().changeScene("mainView");
    }
}