package uneg.bombfx.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.layout.VBox;

import uneg.bombfx.App;
import uneg.bombfx.engine.Engine.LabelAdder;

/**
 * Server class with {@link Networked} operations
 */
public class Server implements Networked {
    public class ClientItem {
        public static int clientsConnected = 0;
        public int id;
        public Socket socket;
        public Boolean listening = false;
        public Thread tmpThread;
        public DataInputStream inputStream;
        public DataOutputStream outputStream;

        public ClientItem(Socket socket) {
            this.socket = socket;
            this.id = ClientItem.clientsConnected;
            ClientItem.clientsConnected++;

            try {
                this.socket.setTcpNoDelay(true);
                this.inputStream = new DataInputStream(this.socket.getInputStream());
                this.outputStream = new DataOutputStream(this.socket.getOutputStream());
            } catch (Exception e) {
                System.err.println("[!!Error]> Error creating client item: " + e.getMessage());
            }
        }
    }

    public ArrayList<ClientItem> clients = new ArrayList<>();
    private Thread connectionThread;
    private ServerSocket hostSocket;
    private int localPort;

    /**
     * @param localPort
     * @throws IOException
     */
    public Server(int localPort) throws Exception {
        this.localPort = localPort;
        try {
            connect(this.localPort);
        } catch (Exception e) {
            throw new Exception("Creating server socket: " + e.getMessage());
        }
    }

    public void connect(int localPort) throws Exception {
        // Crear socket
        this.hostSocket = new ServerSocket(localPort);
    }

    public void listenForConnections(VBox textBox, LabelAdder la) {
        if (connectionThread != null)
            connectionThread.interrupt();
        connectionThread = new Thread(() -> {
            while (!hostSocket.isClosed()) {
                try {
                    ClientItem client = new ClientItem(hostSocket.accept());
                    clients.add(client);

                    listenForPackets(textBox, la);
                } catch (Exception e) {
                    System.err.println("[!!Error]> Connecting with clients: " + e.getMessage());
                    e.printStackTrace();
                    closeEverything();
                    break;
                }
            }
        });
        connectionThread.start();
    }

    public void listenForPackets(VBox textBox, LabelAdder la) {
        for (ClientItem client : clients) {
            if (client.listening)
                continue;
            client.tmpThread = new Thread(() -> {
                while (client.socket.isConnected()) {
                    try {
                        byte flag = client.inputStream.readByte();

                        switch (ConnFlags.fromByte(flag)) {
                            case Message:
                                String message = client.inputStream.readUTF();
                                sendMsgFromClient(client.id, message);
                                message = "[" + client.id + "]: " + message;
                                la.addLabel(message, textBox);
                                break;
                            case PlayerConnected:
                                break;
                            case PlayerDisconnected:
                                break;
                            case CloseConnection:
                                break;
                            case Invalid:
                                throw new Exception("Invalid flag");
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("[!!Error]> Receiving message: " + e.getMessage());
                        e.printStackTrace();
                        closeEverything();
                        break;
                    }
                }
            });
            client.tmpThread.start();
            client.listening = true;
        }
    }

    private void sendMsgFromClient(int id, String message) {
        ConnFlags flag = ConnFlags.Message;
        String msg = "[" + id + "]: " + message;
        try {
            for (ClientItem client : clients) {
                if (client.id == id)
                    continue;
                client.outputStream.writeByte(flag.getAsByte());
                client.outputStream.writeUTF(msg);
                client.outputStream.flush();
            }
        } catch (Exception e) {
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            e.printStackTrace();
            closeEverything();
        }
    }

    public void sendFlagMsg(ConnFlags flag) {
        try {
            for (ClientItem client : clients)
                client.outputStream.writeByte(flag.getAsByte());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            closeEverything();
        }
    }

    public void sendMessage(String message) {
        ConnFlags flag = ConnFlags.Message;
        try {
            for (ClientItem client : clients) {
                client.outputStream.writeByte(flag.getAsByte());
                client.outputStream.writeUTF(message);
                client.outputStream.flush();
            }
        } catch (Exception e) {
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            e.printStackTrace();
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
        // } catch (Exception e) {
        // e.printStackTrace();
        // System.out.println("Error sending message: " + e.getMessage());
        // closeEverything();
        // }
    }

    public void closeEverything() {
        try {
            for (ClientItem client : clients) {
                if (client.socket != null)
                    client.socket.close();
                client.tmpThread.interrupt();
                client.listening = false;
            }
            if (hostSocket != null)
                hostSocket.close();
            if (connectionThread != null)
                connectionThread.interrupt();
            clients.clear();
        } catch (Exception e) {
            System.err.println("Error closing everything: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void interruptConnection() {
        if (connectionThread != null)
            connectionThread.interrupt();
    }
}
