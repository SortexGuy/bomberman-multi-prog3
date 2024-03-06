package uneg.bombfx.engine;

import java.net.InetAddress;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import uneg.bombfx.App;
import uneg.bombfx.components.Bomb;
import uneg.bombfx.components.LevelGrid;
import uneg.bombfx.components.Player;
import uneg.bombfx.networking.Client;
import uneg.bombfx.networking.Server;

/**
 * Engine
 */
public class Engine {
    private int tickCount = 0;
    private double timeSecToTick = 0;

    private GameLoop gameLoop;
    private ArrayList<Player> players;
    private ArrayList<Bomb> bombs;
    private LevelGrid levelGrid;
    private GraphicsContext gContext;

    private boolean isHost = false;
    private Server server;
    private Client client;

    public static interface LabelAdder {
        public void addLabel(String message, VBox textBox);
    }

    public static interface PlayerSyncObj {
        public void syncPos(int id, Point2D pos);
    }

    public void connectServer(int listenPort) throws Exception {
        // Server initialization
        isHost = true;
        server = new Server(listenPort);
    }

    public void connectClient(String serverIP, int serverPort) throws Exception {
        // Client initialization
        InetAddress ip = InetAddress.getByName(serverIP);
        client = new Client(ip, serverPort);
    }

    public void prepare(
            Button sendButton, VBox textBox, TextField chatField, LabelAdder labelAdder) {
        // Message receiving
        if (isHost) {
            server.interruptClients();
            server.listenForConnections();
        }
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error connecting client: " + e.getMessage());
            closeConnection();
            return;
        }
        client.listenForPackets(textBox, labelAdder);

        // Chat setup
        chatField.onKeyReleasedProperty().set(e -> {
            if (e.getCode() != KeyCode.ENTER)
                return;
            String message = chatField.getText();
            if (message.isEmpty())
                return;

            labelAdder.addLabel(message, textBox);
            client.sendMessage(message);

            chatField.clear();
        });

        sendButton.focusTraversableProperty().setValue(false);
        sendButton.setOnAction(e -> {
            String message = chatField.getText();
            if (message.isEmpty())
                return;

            labelAdder.addLabel(message, textBox);
            client.sendMessage(message);

            chatField.clear();
        });
    }

    public void startup(Button sendButton, VBox textBox, TextField messageField, LabelAdder la,
            Canvas gameCanvas) {
        int playerCount = client.getPlayerCount();

        levelGrid = new LevelGrid();

        players = new ArrayList<Player>(playerCount + 1);
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player(i, levelGrid));
            // players.set(i, new Player(i, levelGrid));
        }

        client.setSyncObj(new PlayerSyncObj() {
            public void syncPos(int id, Point2D pos) {
                for (Player p : players) {
                    if (p.getId() != id)
                        continue;

                    p.setPos(pos);
                }
            }
        });
        System.err.println("Starting game, with " + playerCount + " players");

        this.gContext = gameCanvas.getGraphicsContext2D();

        // Message receiving
        if (isHost) {
            server.interruptClients();

            server.interruptConnection();
            server.listenForPackets();
        }
        client.listenForPackets(textBox, la);

        // Chat setup
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
        sendButton.focusTraversableProperty().setValue(false);
        sendButton.setOnAction(e -> {
            String message = messageField.getText();
            if (message.isEmpty())
                return;

            la.addLabel(message, textBox);
            client.sendMessage(message);

            messageField.clear();
        });

        gameLoop = new GameLoop() {
            @Override
            public void tick(double deltaTime) {
                update(deltaTime);
                draw(gameCanvas.getWidth(), gameCanvas.getHeight());
            }

            public void sync(double deltaTime) {
                syncEngine();
            }
        };
    }

    public void start() {
        gameLoop.start();
    }

    private void update(double deltaTime) {
        Player localPlayer = players.get(client.getId());
        localPlayer.update(deltaTime);
        levelGrid.update(deltaTime);
    }

    private void syncEngine() {
        players.get(client.getId()).sync(client);
    }

    private void draw(double gameWidth, double gameHeight) {
        gContext.setFill(Color.GRAY);
        gContext.fillRect(0, 0, gameWidth, gameHeight);

        levelGrid.draw(gContext);

        for (Player p : players) {
            p.draw(gContext);
        }
    }

    public void handleKeyPressed(KeyEvent e) {
        players.get(client.getId()).handleInputPressed(e);
    }

    public void handleKeyReleased(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            closeConnection();
            App.setRoot("views/MainMenuUI");
        }
        players.get(client.getId()).handleInputReleased(e);
    }

    public void closeConnection() {
        if (gameLoop != null)
            gameLoop.stop();
        gameLoop = null;
        if (server != null)
            server.closeEverything();
        if (client != null)
            client.closeEverything();
    }

    public void startHostingGame() {
        server.startGame();
    }

    public void startGame() {
        App.setRoot("views/MainGameUI");
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addBomb(Point2D pos, int id) {
        bombs.add(new Bomb(id, pos, players, levelGrid));
    }
}
