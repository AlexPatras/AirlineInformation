package gui;

import businessLogic.BusinessLogicAPI;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.application.Platform;
import javafx.util.Callback;

/**
 * JavaFX GUIApp
 *
 * @author nl.fontys.prj2.13
 */
public class GUIApp extends Application {

    private BusinessLogicAPI businessLogicAPI;
    private SceneManager sceneManager;
    private static final String initialView = "mainView";

    private final Callback<Class<?>, Object> controllerFactory = (Class<?> c) -> {
        switch (c.getName()) {
            case "gui.PlaneController":
                return new PlaneController(this::getSceneManager, businessLogicAPI.getPlaneManager());
            case "gui.MainController":
                return new MainController(this::getSceneManager, businessLogicAPI.getFlightManager());
            case "gui.AirportController":
                return new AirportController(this::getSceneManager, businessLogicAPI.getAirportManager());
            case "gui.ManagementDashboardController":
                return new ManagementDashboardController(this::getSceneManager);
            case "gui.BookingController":
                return new BookingController(this::getSceneManager, businessLogicAPI.getBookingManager(), businessLogicAPI.getPassengerManager(), businessLogicAPI.getExtrasManager(), businessLogicAPI.getDiscountManager(), businessLogicAPI.getFlightManager());
            case "gui.DiscountsController":
                return new DiscountsController(this::getSceneManager, businessLogicAPI.getDiscountManager());
            case "gui.RegisterFlightController":
                return new RegisterFlightController(this::getSceneManager, businessLogicAPI.getFlightManager());
            case "gui.ManageAccountsController":
                return new ManageAccountsController(this::getSceneManager, businessLogicAPI.getUserManager());
            default:
                return null;
        }
    };

    /**
     * Constructor
     *
     * @param businessLogicAPI to use
     */
    public GUIApp(BusinessLogicAPI businessLogicAPI) {
        this.businessLogicAPI = businessLogicAPI;
    }

    /**
     * initializes the app
     *
     * @return itself
     */
    public GUIApp show() {
        return init(true);
    }

    /**
     * initialization of the program
     *
     * @param startJavaFXToolkit boolean to start the tool kit
     * @return itself
     */
    GUIApp init(boolean startJavaFXToolkit) {
        if (startJavaFXToolkit) {
            Platform.startup(() -> {
            });

            initializeSceneManager();

            Platform.runLater(() -> {
                Stage stage = new Stage();
                try {
                    start(stage);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
        } else {
            initializeSceneManager();
        }
        return this;
    }

    /**
     * Create new SceneManager instance
     */
    private void initializeSceneManager() {
        sceneManager = new SceneManager(controllerFactory, initialView);
    }

    /**
     * Opens of the set stage
     *
     * @param stage to open
     * @throws IOExceptiofailsafe-reportsn
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Airline Information System");
        sceneManager.displayOn(stage, 1300, 800);
    }

    /**
     * Getter method for Scene Manager
     *
     * @return scene manager
     */
    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
