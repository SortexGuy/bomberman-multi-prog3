package uneg.bombfx.views;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import uneg.bombfx.App;

public class MainMenuController {
    @FXML                               // fx:id="mainPane"
    private VBox mainPane;              // Value injected by FXMLLoader
    @FXML                               // fx:id="serverPane"
    private AnchorPane serverPane;      // Value injected by FXMLLoader
    @FXML                               // fx:id="clientPane"
    private AnchorPane clientPane;      // Value injected by FXMLLoader
    @FXML                               // fx:id="serverPort"
    private TextField serverPort;       // Value injected by FXMLLoader
    @FXML                               // fx:id="hostAddr"
    private TextField hostAddr;         // Value injected by FXMLLoader
    @FXML                               // fx:id="clientListenPort"
    private TextField clientListenPort; // Value injected by FXMLLoader

    @FXML
    void OnHostingButtonClicked(ActionEvent event) {
        mainPane.setDisable(true);
        mainPane.setVisible(false);

        serverPane.setDisable(false);
        serverPane.setVisible(true);
    }

    @FXML
    void OnJoiningButtonClicked(ActionEvent event) {
        mainPane.setDisable(true);
        mainPane.setVisible(false);

        clientPane.setDisable(false);
        clientPane.setVisible(true);
    }

    @FXML
    void OnCreateServerButtonClicked(ActionEvent event) {
        App.setHosting(true);
        App.setRoot("views/LobbyUI");
    }

    @FXML
    void OnJoinServerButtonClicked(ActionEvent event) {
        App.setHosting(false);
        App.setRoot("views/LobbyUI");
    }

    @FXML
    void OnBackFromHostButtonClicked(ActionEvent event) {
        serverPane.setDisable(true);
        serverPane.setVisible(false);

        mainPane.setDisable(false);
        mainPane.setVisible(true);
    }

    @FXML
    void OnBackFromJoinButtonClicked(ActionEvent event) {
        clientPane.setDisable(true);
        clientPane.setVisible(false);

        mainPane.setDisable(false);
        mainPane.setVisible(true);
    }

    @FXML
    void OnExitButtonClicked(ActionEvent event) {
        Platform.exit();
    }
}
