package gui;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * @author nl.fontys.prj2.13
 */
public class SceneManager {

    private final Scene scene;
    private final Callback<Class<?>, Object> controllerFactory;

    /**
     * Constructor
     *
     * @param controllerFactory
     * @param initialView
     */
    public SceneManager(Callback<Class<?>, Object> controllerFactory, String initialView) {
        this.controllerFactory = controllerFactory;
        scene = new Scene(loadScene(initialView, null));
    }

    /**
     * Getter for active scene
     *
     * @return scene
     */
    public Scene getScene() {
        return scene;
    }

    public final void changeScene(String view) {
        scene.setRoot(loadScene(view, null));
    }

    public final <T> void changeScene(String view, Consumer<T> consumer) {
        scene.setRoot(loadScene(view, consumer));
    }

    /**
     *
     * @param <T>
     * @param fxml     View to load
     * @param consumer
     * @return Parent file
     * @throws IllegalStateException
     */
    private <T> Parent loadScene(String fxml, Consumer<T> consumer) {
        var fxmlResource = GUIApp.class.getResource(fxml + ".fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlResource);
        fxmlLoader.setControllerFactory(controllerFactory);

        try {
            Parent parent = fxmlLoader.load();
            if (consumer != null) {
                var controller = (T) fxmlLoader.getController();
                consumer.accept(controller);
            }
            return parent;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Method to show the view
     *
     * @param stage  to show
     * @param width  of the window
     * @param height of the window
     */
    void displayOn(Stage stage, int width, int height) {
        stage.setScene(scene);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.show();
    }
}
