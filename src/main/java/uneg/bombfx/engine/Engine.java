package uneg.bombfx.engine;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import uneg.bombfx.App;
import uneg.bombfx.components.Player;
import uneg.bombfx.networking.Client;
import uneg.bombfx.networking.Networked;
import uneg.bombfx.networking.Networked.ConnFlags;
import uneg.bombfx.networking.Server;

/**
 * Engine
 */
public class Engine {
    static private Player player1;
    private GraphicsContext gContext;
    private Server server;
    private Client client;
    private Thread tmpThread;

    private boolean isHost = false;

    public Engine() {
        player1 = new Player(0);
    }

    public static interface LabelAdder {
        public void addLabel(String message, VBox textBox);
    }

    public void connectServer() {
        // Server initialization
        isHost = true;
        tmpThread = new Thread(() -> {
            try {
                server = new Server(4321);
            } catch (Exception e) {
                System.out.println("Error connecting to the network.");
                System.err.println("[!!Error!!] " + e.getMessage());
            }
        });
        tmpThread.start();
    }

    public void connectClient() {
        // Client initialization
        isHost = false;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            client = new Client(ip.getHostName(), 4321);
        } catch (Exception e) {
            System.out.println("Error connecting to the network.");
            System.err.println("[!!Error!!] " + e.getMessage());
        }
    }

    public void startup(Button sendButton, VBox textBox, TextField messageField, LabelAdder la,
            Boolean stopConnecting) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Message receiving
        if (isHost) {
            if (!server.clients.isEmpty()) {
                for (Server.ClientItem c : server.clients)
                    if (c.tmpThread != null)
                        c.tmpThread.interrupt();
            }

            if (stopConnecting) {
                server.interruptConnection();
                server.listenForPackets(textBox, la);
            } else {
                server.listenForConnections(textBox, la);
            }
        } else
            client.listenForPackets(textBox, la);

        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (message.isEmpty())
                return;

            la.addLabel(message, textBox);

            if (isHost)
                server.sendMessage(message);
            else
                client.sendMessage(message);

            messageField.clear();
        });
    }

    public void startup(Button sendButton, VBox textBox, TextField messageField, LabelAdder la,
            GraphicsContext gContext) {
        this.gContext = gContext;
        startup(sendButton, textBox, messageField, la, true);
    }

    public void sendNetworkedMsg(String message) {
        if (isHost)
            server.sendMessage(message);
        else
            client.sendMessage(message);
    }

    public void draw(double gameWidth, double gameHeight) {
        gContext.setFill(Color.GRAY);
        gContext.fillRect(0, 0, gameWidth, gameHeight);
        player1.draw(gContext);
    }

    public void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            if (isHost)
                server.closeEverything();
            else
                client.closeEverything();
            App.setRoot("views/MainMenuUI");
        }

        // player1.handleInput(e, connection);
    }

    public void closeConnection() {
        if (server != null)
            server.closeEverything();
        if (client != null)
            client.closeEverything();
        if (tmpThread != null)
            tmpThread.interrupt();
    }

    public static void setPlayerPosition(Point2D newPlayerPos) {
        player1.setPos(newPlayerPos);
    }

    public void startGame() {
        if (isHost) {
            server.sendFlagMsg(ConnFlags.StartGame);
        }
    }
}
