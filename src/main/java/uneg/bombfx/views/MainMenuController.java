package uneg.bombfx.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import uneg.bombfx.App;

/**
 * Sample Skeleton for 'MainMenuUI.fxml' Controller Class
 */
public class MainMenuController {
    @FXML                         // fx:id="mainButtonsCont"
    private VBox mainButtonsCont; // Value injected by FXMLLoader

    @FXML                        // fx:id="serverButton"
    private Button serverButton; // Value injected by FXMLLoader

    @FXML                      // fx:id="joinButton"
    private Button joinButton; // Value injected by FXMLLoader

    @FXML                      // fx:id="exitButton"
    private Button exitButton; // Value injected by FXMLLoader

    @FXML
    void OnServerButtonClicked(ActionEvent event) {
        App.setHosting(true);
        App.setRoot("views/LobbyUI");
    }

    @FXML
    void OnJoinButtonClicked(ActionEvent event) {
        App.setHosting(false);
        App.setRoot("views/LobbyUI");
    }

    @FXML
    void OnExitButtonClicked(ActionEvent event) {
        Platform.exit();
    }
}
