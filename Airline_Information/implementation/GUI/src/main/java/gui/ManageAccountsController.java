/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package gui;

import businessLogic.UserManager;
import dataRecords.UserData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

/**
 * FXML Controller class
 */
public class ManageAccountsController implements Initializable {

    @FXML
    private TextField createLastName;
    @FXML
    private TextField createFirstName;
    @FXML
    private ComboBox<String> createRole;
    @FXML
    private TextField createPassword;
    @FXML
    private TextField createPasswordCheck;

    @FXML
    private Label createLastNameError;
    @FXML
    private Label createFirstNameError;
    @FXML
    private Label createPasswordError;

    @FXML
    private TableView<UserData> userTable;
    @FXML
    private TableColumn<UserData, String> lastNameColumn;
    @FXML
    private TableColumn<UserData, String> firstNameColumn;
    @FXML
    private TableColumn<UserData, String> roleColumn;
    @FXML
    private TextField editLastName;
    @FXML
    private TextField editFirstName;
    @FXML
    private ComboBox<String> editRole;
    @FXML
    private TextField editPassword;
    @FXML
    private TextField editPasswordCheck;

    @FXML
    private Label editLastNameError;
    @FXML
    private Label editFirstNameError;
    @FXML
    private Label editPasswordError;

    //Buttons declared in controller class for declaring disabled or enabled
    @FXML
    private Button editUserButton;
    @FXML
    private Button removeUserButton;
    private final Supplier<SceneManager> sceneManagerSupplier;
    private final UserManager userManager;
    private UserData userToEdit;
    
    public ManageAccountsController(Supplier<SceneManager> sceneManagerSupplier, UserManager userManager) {
        this.sceneManagerSupplier = sceneManagerSupplier;
        this.userManager = userManager;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<UserData, String>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<UserData, String>("firstName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<UserData, String>("role"));
        List<String> roles = new ArrayList<>();
        roles.add("Sales Employee");
        roles.add("Sales Officer");
        roles.add("Sales Manager");
        createRole.getItems().setAll(roles);
        editRole.getItems().setAll(roles);
        reset();
        userTable.setOnMouseClicked((MouseEvent event) -> {
            userToEdit = getUser();
        });
    }

    /**
     * Method to pass a new registry to the business logic to store inside the database
     * @param event 
     */
    @FXML
    private void addUser(ActionEvent event) {
        String lastName = createLastName.getText();
        String firstName = createFirstName.getText();
        String role = createRole.getValue();
        String password = createPassword.getText();
        String passwordCheck = createPasswordCheck.getText();

        if (validUserCreate(lastName, firstName, password, passwordCheck)) {
//            String encryptedPassword = encrypt(password);
//            userManager.createUser(new UserData(lastName, firstName, role, encryptedPassword));
            userManager.createUser(lastName, firstName, role, password);
            reset();
        }
    }

    /**
     * Method to update an existing entry after selecting from the table and updating a value
     * @param event 
     */
    @FXML
    private void updateUser(ActionEvent event) {
        int id = userToEdit.getId();
        String lastName = editLastName.getText();
        String firstName = editFirstName.getText();
        String role = editRole.getValue();
        String password = editPassword.getText();
        String passwordCheck = editPasswordCheck.getText();
        
        if (validUserUpdate(lastName, firstName, password, passwordCheck) && userToEdit != null) {
            userToEdit.setLastName(lastName);
            userToEdit.setFirstName(firstName);
            userToEdit.setRole(role);
            userToEdit.setPassword(password);
            userManager.updateUser(userToEdit);
            reset();
        }
    }

    /**
     * Method to remove an entry from the database after selecting from the table
     * @param event 
     */
    @FXML
    private void removeUser(ActionEvent event) {
        userManager.removeUser(userToEdit);
        reset();
    }

    /**
     * Method to change the active scene to the  management dashboard scene
     * @param event 
     */
    @FXML
    private void returnToMainPage(ActionEvent event) {
        sceneManagerSupplier.get().changeScene("managementDashboardView");
    }

    /**
     * Helper method to empty all text fields, set comboboxes to initial value and update the table
     */
    private void reset() {
        createFirstName.setText("");
        createLastName.setText("");
        createPassword.setText("");
        createPasswordCheck.setText("");
        createRole.getSelectionModel().selectFirst();

        createFirstNameError.setText("");
        createLastNameError.setText("");
        createPasswordError.setText("");

        editFirstName.setText("");
        editLastName.setText("");
        editPassword.setText("");
        editPasswordCheck.setText("");
        editRole.getSelectionModel().selectFirst();

        editFirstNameError.setText("");
        editLastNameError.setText("");
        editPasswordError.setText("");

        List<UserData> users = userManager.getAllUsers();
        System.out.println(users);
        userTable.getItems().setAll(users);
        
        editUserButton.setDisable(true);
        removeUserButton.setDisable(true);
        editFirstName.setDisable(true);
        editLastName.setDisable(true);
        editPassword.setDisable(true);
        editPasswordCheck.setDisable(true);
        editRole.setDisable(true);
    }

    
    /**
     * Helper method for creating and updating entries. Checks entries for errors
     * @param lastName
     * @param firstName
     * @param password
     * @param passwordCheck
     * @return true or false whether the entry is valid or not
     */
    private boolean validUserCreate(String lastName, String firstName, String password, String passwordCheck) {

        if (lastName.matches("^[A-Z]{1}+[a-z]*")) {
            createLastNameError.setText("");
        } else if (lastName.isBlank()) {
            createLastNameError.setText("Enter a last name");
        } else if (lastName.matches("^[a-z]+[a-z]*")) {
            createLastNameError.setText("Please write the first letter in upper case");
        } else if (lastName.matches("[^a-z]*")) {
            createLastNameError.setText("Please use only letters");
        } else {
            createLastNameError.setText("Unknown Error");
        }

        if (firstName.matches("^[A-Z]{1}+[a-z]*")) {
            createFirstNameError.setText("");
        } else if (firstName.isBlank()) {
            createFirstNameError.setText("Enter a first name");
        } else if (firstName.matches("^[a-z]+[a-z]*")) {
            createFirstNameError.setText("Please write the first letter in upper case");
        } else if (firstName.matches("[^a-z]*")) {
            createFirstNameError.setText("Please use only letters");
        } else {
            createFirstNameError.setText("Unknown Error");
        }

        if (password.length() < 12 && password.length() > 0) {
            createPasswordError.setText("Password too short");
        } else if (!password.equals(passwordCheck)) {
            createPasswordError.setText("Passwords have to match");
        } else if (password.length() <= 0 || password.length() > 0 && passwordCheck.length() <= 0) {
            createPasswordError.setText("Enter the password in both fields");
        } else {
            boolean hasUppercase = false;
            boolean hasLowercase = false;
            boolean hasDigit = false;

            for (char character : password.toCharArray()) {
                if (Character.isDigit(character)) {
                    hasDigit = true;
                } else if (Character.isLowerCase(character)) {
                    hasLowercase = true;
                } else if (Character.isUpperCase(character)) {
                    hasUppercase = true;
                }
            }

            if (hasUppercase && hasLowercase && hasDigit) {
                createPasswordError.setText("");
            } else {
                createPasswordError.setText("Enter a password containing at least one upper and lowercase letter and one number");
            }
        }

        if (createFirstNameError.getText().isBlank() &&
                createLastNameError.getText().isBlank() &&
                createPasswordError.getText().isBlank()) {
            return true;
        }

        return false;
    }
    
    private boolean validUserUpdate(String lastName, String firstName, String password, String passwordCheck) {

        if (lastName.matches("^[A-Z]{1}+[a-z]*")) {
            editLastNameError.setText("");
        } else if (lastName.isBlank()) {
            editLastNameError.setText("Enter a last name");
        } else if (lastName.matches("^[a-z]+[a-z]*")) {
            editLastNameError.setText("Please write the first letter in upper case");
        } else if (lastName.matches("[^a-z]*")) {
            editLastNameError.setText("Please use only letters");
        } else {
            editLastNameError.setText("Unknown Error");
        }

        if (firstName.matches("^[A-Z]{1}+[a-z]*")) {
            editFirstNameError.setText("");
        } else if (firstName.isBlank()) {
            editFirstNameError.setText("Enter a last name");
        } else if (firstName.matches("^[a-z]+[a-z]*")) {
            editFirstNameError.setText("Please write the first letter in upper case");
        } else if (firstName.matches("[^a-z]*")) {
            editFirstNameError.setText("Please use only letters");
        } else {
            editFirstNameError.setText("Unknown Error");
        }

        if (password.length() < 12 && password.length() > 0) {
            editPasswordError.setText("Password too short");
        } else if (!password.equals(passwordCheck)) {
            editPasswordError.setText("Passwords have to match");
        } else if (password.length() <= 0 || password.length() > 0 && passwordCheck.length() <= 0) {
            editPasswordError.setText("Enter the password in both fields");
        } else {
            boolean hasUppercase = false;
            boolean hasLowercase = false;
            boolean hasDigit = false;

            for (char character : password.toCharArray()) {
                if (Character.isDigit(character)) {
                    hasDigit = true;
                } else if (Character.isLowerCase(character)) {
                    hasLowercase = true;
                } else if (Character.isUpperCase(character)) {
                    hasUppercase = true;
                }
            }

            if (hasUppercase && hasLowercase && hasDigit) {
                editPasswordError.setText("");
            } else {
                editPasswordError.setText("Enter a password containing at least one upper and lowercase letter and one number");
            }
        }

        if (editFirstNameError.getText().isBlank() &&
                editLastNameError.getText().isBlank() &&
                editPasswordError.getText().isBlank()) {
            return true;
        }

        return false;
    }

    /**
     * Decrypt a hashed string (not implemented)
     * @param password hashed password
     * @return decrypted string
     */
    private String decrypt(String password) {
        return "";
    }

    /**
     * Encrypt string for security
     * @param password not hashed
     * @return hashed string
     */
    private String encrypt(String password) {
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(32, 64, 1, 15 + 1024, 2);

        String encodedPassword = encoder.encode(password);
        
        if (encoder.matches(password, encodedPassword)) {
            return encodedPassword;
        } else {
            throw new RuntimeException("Something went wrong during the encryption");
        }
    }

    private UserData getUser() {
        if(userTable.getSelectionModel().getSelectedItems() != null) {
            UserData user = userTable.getSelectionModel().getSelectedItems().get(0);
            editFirstName.setText(user.getFirstName());
            editLastName.setText(user.getLastName());
            if(user.getRole().equals("Sales Manager")) {
                editRole.getSelectionModel().select(2);
            } else if(user.getRole().equals("Sales Officer")) {
                editRole.getSelectionModel().select(1);
            } else {
                editRole.getSelectionModel().select(0);
            }
//            editPassword.setText(decrypt(user.getPassword()));
//            editPasswordCheck.setText(decrypt(user.getPassword()));
            editPassword.setText(user.getPassword());
            editPasswordCheck.setText(user.getPassword());
            
            editFirstName.setDisable(false);
            editLastName.setDisable(false);
            editRole.setDisable(false);
            editPassword.setDisable(false);
            editPasswordCheck.setDisable(false);
            editUserButton.setDisable(false);
            removeUserButton.setDisable(false);
            return user;
        }
        return null;
    }
}
