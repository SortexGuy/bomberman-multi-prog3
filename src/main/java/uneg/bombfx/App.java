package uneg.bombfx;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    private static Scene scene;
    private static boolean hosting = false;
    // public static final int WIDTH = 640;
    // public static final int HEIGHT = 480;
    // public static final int FPS = 60;

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

    private static Parent loadFromFXML(String fxml) throws IOException {
        URL location = App.class.getResource(fxml + ".fxml");
        Parent hierarchy = new AnchorPane();
        hierarchy = FXMLLoader.load(location);
        return hierarchy;
    }

    public static void setRoot(String fxml) {
        try {
            Parent hierarchy = App.loadFromFXML(fxml);
            scene.setRoot(hierarchy);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static Scene getScene() {
        return scene;
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
}
