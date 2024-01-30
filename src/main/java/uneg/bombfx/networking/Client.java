package uneg.bombfx.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Point2D;
import javafx.scene.layout.VBox;

import uneg.bombfx.App;
import uneg.bombfx.engine.Engine.LabelAdder;

/**
 * Client class with {@link Networked} operations
 */
public class Client implements Networked {
    public Thread tmpThread;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private InetAddress serverAddr;
    private int serverPort;

    public Client(String serverIp, int serverPort) throws Exception {
        this.serverAddr = InetAddress.getByName(serverIp);
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
        // this.outputStream.writeByte(ConnFlags.PlayerConnected.getAsByte());
        // this.outputStream.flush();
    }

    public void listenForPackets(VBox textBox, LabelAdder la) {
        if (tmpThread != null)
            tmpThread.interrupt();
        tmpThread = new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    byte flag = inputStream.readByte();

                    switch (ConnFlags.fromByte(flag)) {
                        case Message:
                            String message = inputStream.readUTF();
                            la.addLabel(message, textBox);
                            break;
                        case PlayerConnected:
                            System.err.println("Player connected");
                            break;
                        case PlayerDisconnected:
                            System.err.println("Player disconnected");
                            break;
                        case CloseConnection:
                            System.err.println("Closing connection");
                            break;
                        case StartGame:
                            App.setRoot("views/MainGameUI");
                            break;
                        case Invalid:
                            throw new Exception("Invalid flag");
                        default:
                            break;
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
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            closeEverything();
        }
    }

    @Override
    public void receiveSyncState(ByteBuffer syncedState) {
        // byte flags = syncedState.get();
        // if (flags == 0b00000001) {
        // Point2D newPlayerPos = new Point2D(syncedState.getFloat(),
        // syncedState.getFloat());
        // MainGameController.setPlayerPosition(newPlayerPos);
        // }
    }

    @Override
    public void sendSyncState(Point2D state) {
        // byte flags = 0b00000001;
        // ByteBuffer syncedState = ByteBuffer.allocate(9);
        // syncedState.put(flags);
        // syncedState.putFloat((float) state.getX());
        // syncedState.putFloat((float) state.getY());
        //
        // try {
        // DatagramPacket packet =
        // new DatagramPacket(syncedState.array(), syncedState.array().length);
        // socket.send(packet);
        // } catch (Exception e) {
        // e.printStackTrace();
        // System.out.println("Error sending message: " + e.getMessage());
        // closeEverything(socket);
        // }
    }

    public void closeEverything() {
        try {
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
}
