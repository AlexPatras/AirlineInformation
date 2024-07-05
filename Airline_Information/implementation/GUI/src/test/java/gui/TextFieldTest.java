package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TextFieldTest extends Parent {
        private TextField numberOfSeats;

        @Test
        public void testFXML() throws Exception {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MyFXML.fxml"));
                Parent root = loader.load();
                numberOfSeats = (TextField) lookup("numberOfSeats");
                assertNotNull(numberOfSeats);
                assertEquals("value is not null", numberOfSeats.getText());
        }
}