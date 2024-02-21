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

    AnimationTimer updateThread = null;
    AnimationTimer drawThread = null;

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

        appEngine.startup(sendButton, textBox, messageField, new Engine.LabelAdder() {
            public void addLabel(String message, VBox myTextBox) {
                MainGameController.addLabel(message, myTextBox);
            }
        }, gameCanvas);

        // Input events
        App.getScene().addEventHandler(
                KeyEvent.KEY_PRESSED, e -> { appEngine.handleKeyPressed(e); });
        App.getScene().addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (!messageField.isFocused() && e.getCode() == KeyCode.TAB) {
                messageField.requestFocus();
            }
            appEngine.handleKeyReleased(e);
        });

        gameCanvas.setFocusTraversable(true);
        gameCanvas.requestFocus();

        appEngine.start();
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
