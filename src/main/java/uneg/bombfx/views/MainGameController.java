/**
 * Sample Skeleton for 'MainGameUI.fxml' Controller Class
 */
package uneg.bombfx.views;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import uneg.bombfx.App;
import uneg.bombfx.components.Player;
import uneg.bombfx.networking.Client;
import uneg.bombfx.networking.Server;

public class MainGameController implements Initializable {
    @FXML                           // fx:id="gameCanvas"
    private Canvas gameCanvas;      // Value injected by FXMLLoader
    @FXML                           // fx:id="messageField"
    private TextField messageField; // Value injected by FXMLLoader
    @FXML                           // fx:id="scrollCont"
    private ScrollPane scrollCont;  // Value injected by FXMLLoader
    @FXML                           // fx:id="sendButton"
    private Button sendButton;      // Value injected by FXMLLoader
    @FXML                           // fx:id="textBox"
    private VBox textBox;           // Value injected by FXMLLoader

    private GraphicsContext gContext;
    private Player player1;
    private Server server;
    private Client client;
    private boolean isHost = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isHost = App.isHosting();
        gContext = gameCanvas.getGraphicsContext2D();
        initializeGame();
        // Input events
        App.getScene().addEventHandler(KeyEvent.KEY_PRESSED, e -> { handleKeyPressed(e); });
        // Draw event
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                drawGame();
            }
        }.start();
    }

    private void initializeGame() {
        // Server initialization
        if (isHost) {
            try {
                server = new Server(new ServerSocket(4321));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error creating server");
            }
        } else {
            try {
                client = new Client(new Socket("localhost", 4321));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error creating server");
            }
        }

        // ChatBox Setup
        textBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                    Number newValue) {
                scrollCont.setVvalue((double) newValue);
            }
        });

        if (isHost) {
            server.receiveMessage(textBox);
            _sendMessage();
        } else {
            client.receiveMessage(textBox);
        }

        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (message.isEmpty())
                return;
            MainGameController.addLabel(message, textBox);

            if (isHost)
                server.sendMessage(message);
            else
                client.sendMessage(message);

            messageField.clear();
        });

        player1 = new Player(0);
    }

    // Temporal
    private void _sendMessage() {
        String message = "Hello Client from Server!";

        MainGameController.addLabel(message, textBox);

        if (isHost)
            server.sendMessage(message);
        else
            client.sendMessage(message);
    }

    public static void addLabel(String message, VBox myTextBox) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(2, 2, 2, 4));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        textFlow.setStyle("-fx-background-color: rgba(25, 25, 25, 128) "
                + "-fx-color: rgba(255, 255, 255, 255)");
        textFlow.setPadding(new Insets(2, 4, 2, 4));
        text.setFill(new Color(0.2, 0.2, 0.2, 0.4));

        box.getChildren().add(textFlow);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                myTextBox.getChildren().add(box);
            }
        });
    }

    private void drawGame() {
        gContext.setFill(Color.WHITE);
        gContext.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        gContext.setFill(Color.WHITE);
        gContext.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        player1.draw(gContext);
    }

    private void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            App.setRoot("views/MainMenuUI");
        }
        player1.handleInput(e);
    }
}
