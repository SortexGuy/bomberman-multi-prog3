package uneg.bombfx.views;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import uneg.bombfx.App;
import uneg.bombfx.engine.Engine;

public class LobbyController implements Initializable {
    @FXML                        // fx:id="backButton"
    private Button backButton;   // Value injected by FXMLLoader
    @FXML                        // fx:id="chatField"
    private TextField chatField; // Value injected by FXMLLoader
    @FXML                        // fx:id="friendsLabel"
    private Label friendsLabel;  // Value injected by FXMLLoader
    @FXML                        // fx:id="playersBox"
    private VBox playersBox;     // Value injected by FXMLLoader
    @FXML                        // fx:id="sendButton"
    private Button sendButton;   // Value injected by FXMLLoader
    @FXML                        // fx:id="startButton"
    private Button startButton;  // Value injected by FXMLLoader
    @FXML                        // fx:id="textBox"
    private VBox textBox;        // Value injected by FXMLLoader
    @FXML                        // fx:id="youareLabel"
    private Label youareLabel;   // Value injected by FXMLLoader

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.setGameEngine(new Engine());
        Engine appEngine = App.getGameEngine();

        if (App.isHosting())
            appEngine.connectServer(4321);

        appEngine.connectClient("localhost", 4321);

        appEngine.startup(sendButton, textBox, chatField, new Engine.LabelAdder() {
            public void addLabel(String message, VBox myTextBox) {
                LobbyController.addLabel(message, myTextBox);
            }
        }, false);

        backButton.setOnAction(e -> {
            appEngine.closeConnection();
            App.setGameEngine(null);
            App.setRoot("views/MainMenuUI");
        });

        startButton.disableProperty().setValue(!App.isHosting());
        startButton.setOnAction(e -> { appEngine.startHostingGame(); });
    }

    public static void addLabel(String message, VBox myTextBox) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(2, 2, 2, 4));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        // textFlow.setStyle("-fx-background-color: rgba(25, 25, 25, 128); "
        // + "-fx-color: rgb(255, 255, 255);");
        textFlow.setPadding(new Insets(2, 4, 2, 4));
        text.setFill(new Color(0.2, 0.2, 0.2, 0.4));

        box.getChildren().add(textFlow);
        Platform.runLater(() -> { myTextBox.getChildren().add(box); });
    }
}
