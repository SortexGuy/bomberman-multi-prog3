package uneg.bombfx.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Sample Skeleton for 'MainMenuUI.fxml' Controller Class
 */
public class MainMenuController {

    @FXML // fx:id="mainButtonsCont"
    private VBox mainButtonsCont; // Value injected by FXMLLoader

    @FXML // fx:id="serverButton"
    private Button serverButton; // Value injected by FXMLLoader

    @FXML // fx:id="joinButton"
    private Button joinButton; // Value injected by FXMLLoader

    @FXML // fx:id="exitButton"
    private Button exitButton; // Value injected by FXMLLoader

    @FXML
    void OnServerButtonClicked(ActionEvent event) {

    }

    @FXML
    void OnJoinButtonClicked(ActionEvent event) {

    }

    @FXML
    void OnExitButtonClicked(ActionEvent event) {

        Platform.exit();
    }
}
