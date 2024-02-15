/**
 * Sample Skeleton for 'MainGameUI.fxml' Controller Class
 */
package uneg.bombfx.views;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
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
import uneg.bombfx.engine.Engine;

public class MainGameController implements Initializable {
    @FXML
    private Canvas gameCanvas;
    @FXML
    private TextField messageField;
    @FXML
    private ScrollPane scrollCont;
    @FXML
    private Button sendButton;
    @FXML
    private VBox textBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Engine appEngine = App.getGameEngine();
        // ChatBox Setup
        textBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                    Number newValue) {
                scrollCont.setVvalue((double) newValue);
            }
        });

        try {
            System.err.println("Waiting 2 seconds...");
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        appEngine.startup(sendButton, textBox, messageField, new Engine.LabelAdder() {
            public void addLabel(String message, VBox myTextBox) {
                MainGameController.addLabel(message, myTextBox);
            }
        }, gameCanvas.getGraphicsContext2D());

        // Input events
        App.getScene().addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (!messageField.isFocused() && e.getCode() == KeyCode.M) {
                messageField.requestFocus();
            }
            appEngine.handleKeyPressed(e);
        });

        // Draw event
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                appEngine.draw(gameCanvas.getWidth(), gameCanvas.getHeight());
            }
        }.start();

        gameCanvas.setFocusTraversable(true);
        gameCanvas.requestFocus();
    }

    public static void addLabel(String message, VBox myTextBox) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(2, 2, 2, 4));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);

        // textFlow.setStyle("-fx-color: rgb(255, 255, 255);");
        textFlow.setPadding(new Insets(2, 4, 2, 4));
        text.setFill(new Color(0.2, 0.2, 0.2, 0.4));

        box.getChildren().add(textFlow);
        Platform.runLater(() -> { myTextBox.getChildren().add(box); });
    }
}
