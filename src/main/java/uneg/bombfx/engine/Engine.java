package uneg.bombfx.engine;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javafx.event.EventType;
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
import uneg.bombfx.networking.Server;

/**
 * Engine
 */
public class Engine {
    private Player[] players;
    private GraphicsContext gContext;
    private Server server;
    private Client client;
    private Thread tmpThread;

    private boolean isHost = false;

    public Engine() {
    }

    public static interface LabelAdder {
        public void addLabel(String message, VBox textBox);
    }

    public static interface PlayerSyncObj {
        public void syncPos(int id, Point2D pos);
    }

    public void connectServer(int listenPort) {
        // Server initialization
        isHost = true;
        tmpThread = new Thread(() -> {
            try {
                server = new Server(listenPort);
            } catch (Exception e) {
                System.out.println("Error connecting to the network.");
                System.err.println("[!!Error!!] " + e.getMessage());
            }
        });
        tmpThread.start();
        try {
            System.err.println("Waiting 2 seconds...");
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectClient(String serverIP, int serverPort) {
        // Client initialization
        try {
            InetAddress ip = InetAddress.getByName(serverIP);
            client = new Client(ip, serverPort);
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
            server.interruptClients();

            if (stopConnecting) {
                server.interruptConnection();
                server.listenForPackets();
            } else {
                server.listenForConnections();
            }
        }
        client.listenForPackets(textBox, la);

        messageField.onKeyReleasedProperty().set(e -> {
            if (e.getCode() != KeyCode.ENTER)
                return;
            String message = messageField.getText();
            if (message.isEmpty())
                return;

            la.addLabel(message, textBox);
            client.sendMessage(message);

            messageField.clear();
        });
        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (message.isEmpty())
                return;

            la.addLabel(message, textBox);
            client.sendMessage(message);

            messageField.clear();
        });
    }

    public void startup(Button sendButton, VBox textBox, TextField messageField, LabelAdder la,
            GraphicsContext gContext) {
        int playerCount = client.getPlayerCount();

        if (players == null) {
            players = new Player[playerCount];
        }
        for (int i = 0; i < playerCount; i++) {
            players[i] = new Player(i);
        }
        System.err.println("Starting game, with " + playerCount + " players");

        this.gContext = gContext;
        startup(sendButton, textBox, messageField, la, true);
    }

    public void draw(double gameWidth, double gameHeight) {
        gContext.setFill(Color.GRAY);
        gContext.fillRect(0, 0, gameWidth, gameHeight);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int size = 48;
                gContext.setFill(Color.BLACK);
                gContext.fillRect(i * size, j * size, size, size);
                gContext.setFill(Color.WHITE);
                gContext.rect(i * size, j * size, size, size);
            }
        }

        for (Player p : players) {
            p.draw(gContext);
        }
    }

    public void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            if (isHost)
                server.closeEverything();

            client.closeEverything();
            App.setRoot("views/MainMenuUI");
        }
        for (Player p : players) {
            if (p.getId() == client.getId())
                p.handleInput(e, client);
        }
    }

    public void closeConnection() {
        if (server != null)
            server.closeEverything();
        if (client != null)
            client.closeEverything();
        if (tmpThread != null)
            tmpThread.interrupt();
    }

    public void startHostingGame() {
        server.startGame();
    }

    public void startGame() {
        client.setSyncObj(new PlayerSyncObj() {
            public void syncPos(int id, Point2D pos) {
                for (Player p : players) {
                    if (p.getId() != id)
                        continue;

                    p.setPos(pos);
                }
            }
        });
        App.setRoot("views/MainGameUI");
    }

    public Player[] getPlayers() {
        return players;
    }
}
