package uneg.bombfx;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import uneg.bombfx.engine.Engine;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
    private static Engine gameEngine;
    private static boolean hosting = false;

    public static void setRoot(String fxml) {
        try {
            Parent hierarchy = App.loadFromFXML(fxml);
            scene.setRoot(hierarchy);
        } catch (Exception e) {
            System.err.println("[!!Error] Could not load FXML: " + fxml);
            System.err.println("[!!Error] Error message: " + e.getMessage());
        }
    }

    public static Scene getScene() {
        return scene;
    }

    public static Engine getGameEngine() {
        return gameEngine;
    }

    public static void setGameEngine(Engine gameEngine) {
        App.gameEngine = gameEngine;
    }

    public static boolean isHosting() {
        return hosting;
    }

    public static void setHosting(boolean hosting) {
        App.hosting = hosting;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static Parent loadFromFXML(String fxml) throws IOException {
        URL location = App.class.getResource(fxml + ".fxml");
        Parent hierarchy = new AnchorPane();
        hierarchy = FXMLLoader.load(location);
        return hierarchy;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent hierarchy = App.loadFromFXML("views/MainMenuUI");
        scene = new Scene(hierarchy, 960, 540);

        stage.setTitle("Man de las Bombas FX");
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();

        stage.setScene(scene);
        stage.show();
    }
}
