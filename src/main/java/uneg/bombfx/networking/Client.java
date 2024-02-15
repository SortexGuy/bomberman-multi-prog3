package uneg.bombfx.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Point2D;
import javafx.scene.layout.VBox;

import uneg.bombfx.App;
import uneg.bombfx.engine.Engine.LabelAdder;
import uneg.bombfx.engine.Engine.PlayerSyncObj;

/**
 * Client class with {@link Networked} operations
 */
public class Client implements Networked {
    public Thread tmpThread;

    private int id;
    private int playerCount;
    private PlayerSyncObj syncObj;

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private InetAddress serverAddr;
    private int serverPort;

    public Client(InetAddress serverIp, int serverPort) throws Exception {
        this.serverAddr = serverIp;
        this.serverPort = serverPort;
        try {
            connect(this.serverAddr, this.serverPort);
        } catch (Exception e) {
            throw new Exception("Error creating client socket: " + e.getMessage());
        }
    }

    public void connect(InetAddress serverAddr, int serverPort) throws Exception {
        this.socket = new Socket(serverAddr, serverPort);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.socket.setTcpNoDelay(true);

        try {
            System.err.println("Waiting 1 seconds...");
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                byte flag = inputStream.readByte();
                if (flag != ConnFlags.SendIds.getAsByte()) {
                    throw new Exception("Wrong flag for id");
                }
                this.id = inputStream.readInt();
                System.err.println("\n\n ->>> Connected to server with id: " + this.id);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("[!!Error]> Receiving id: " + e.getMessage());
            }
        }).start();
    }

    public void listenForPackets(VBox textBox, LabelAdder la) {
        System.err.println("Listening for packets...");
        if (tmpThread != null) {
            System.err.println("Interrupting thread...");
            tmpThread.interrupt();
        }
        tmpThread = null;
        System.err.println("Creating thread...");
        tmpThread = new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    byte flag = inputStream.readByte();

                    switch (ConnFlags.fromByte(flag)) {
                        case Message:
                            String message = inputStream.readUTF();
                            System.err.println("Received message: " + message);
                            la.addLabel(message, textBox);
                            break;
                        case CloseConnection:
                            closeEverything();
                            App.setRoot("views/MainMenuUI");
                            break;
                        case StartGame:
                            playerCount = inputStream.readInt();
                            System.err.println("Starting game, with " + playerCount + " players");
                            App.getGameEngine().startGame();
                            break;
                        case SyncPlayer:
                            if (syncObj == null)
                                break;
                            handlePlayerSync();
                            break;
                        // case PlayerConnected:
                        case PlayerDisconnected:
                            break;
                        case Invalid:
                        default:
                            break;
                            // throw new Exception("Invalid flag");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("[!!Error]> Receiving message: " + e.getMessage());
                    closeEverything();
                    break;
                }
            }
        });
        tmpThread.start();
    }

    private void handlePlayerSync() throws Exception {
        // Handle pos
        int targetId = inputStream.readInt();
        Point2D targetPos = new Point2D(inputStream.readFloat(), inputStream.readFloat());

        syncObj.syncPos(targetId, targetPos);
    }

    public void sendFlagMsg(ConnFlags flag) {
        try {
            outputStream.writeByte(flag.getAsByte());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            closeEverything();
        }
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeByte(ConnFlags.Message.getAsByte());
            outputStream.writeUTF(message);
            System.err.println("Sending message: " + outputStream.toString());
            System.err.println("With socket: " + socket.getInetAddress());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            closeEverything();
        }
    }

    public void sendSyncPPos(Point2D pos) {
        ConnFlags flag = ConnFlags.SyncPlayer;
        try {
            outputStream.writeByte(flag.getAsByte());
            outputStream.writeFloat((float) pos.getX());
            outputStream.writeFloat((float) pos.getY());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            closeEverything();
        }
    }

    public int waitForId() {
        sendFlagMsg(ConnFlags.SendIds);
        try {
            byte flag = inputStream.readByte();
            if (flag == ConnFlags.SendIds.getAsByte()) {
                return inputStream.readInt();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[!!Error]> Receiving ID: " + e.getMessage());
        }
        return -1;
    }

    public void closeEverything() {
        try {
            outputStream.writeByte(ConnFlags.PlayerDisconnected.getAsByte());
            outputStream.flush();
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
            if (socket != null)
                socket.close();
            if (tmpThread != null)
                tmpThread.interrupt();
        } catch (Exception e) {
            System.err.println("Error closing everything: " + e.getMessage());
        }
    }

    public int getId() {
        return id;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setSyncObj(PlayerSyncObj syncObj) {
        this.syncObj = syncObj;
    }
}
