package uneg.bombfx;

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final int FPS = 60;

    @Override
    public void start(Stage stage) throws Exception {
        URL location = getClass().getResource("views/MainMenuUI.fxml");

        AnchorPane hierarchy = new AnchorPane();
        hierarchy = FXMLLoader.load(location);
        Scene scene = new Scene(hierarchy, 960, 540);

        stage.setTitle("Man de las Bombas FX");
        stage.centerOnScreen();
        stage.setFullScreen(false);
        stage.setMaximized(false);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
