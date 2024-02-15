package uneg.bombfx.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javafx.geometry.Point2D;

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

        public void close() {
            listening = false;
            tmpThread.interrupt();
            try {
                socket.close();
                inputStream.close();
                outputStream.close();
            } catch (Exception e) {
                System.err.println("[!!Error]> Closing client");
                e.printStackTrace();
                closeEverything();
            }
        }
    }

    private ArrayList<ClientItem> clients = new ArrayList<>();
    private Thread connectionThread;
    private ServerSocket hostSocket;
    private int localPort;

    public Server(int localPort) throws Exception {
        clients.clear();
        ClientItem.clientsConnected = 0;

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

    public void listenForConnections() {
        if (connectionThread != null)
            connectionThread.interrupt();
        connectionThread = new Thread(() -> {
            while (!hostSocket.isClosed()) {
                if (clients.size() >= 4) {
                    try {
                        System.err.println("Waiting 2 seconds...");
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                try {
                    ClientItem client = new ClientItem(hostSocket.accept());
                    clients.add(client);
                    System.err.println("Connecting client with id: " + client.id);
                    client.outputStream.writeByte(ConnFlags.SendIds.getAsByte());
                    client.outputStream.writeInt(client.id);
                    client.outputStream.flush();

                    listenForPackets();
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

    public void listenForPackets() {
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
                                break;
                            case PlayerDisconnected:
                                client.close();
                                if (clients.indexOf(client) == 0) {
                                    closeEverything();
                                    break;
                                }
                                clients.remove(client);
                                break;
                            case SyncPlayer:
                                syncPlayerState(client);
                                break;
                            case SendIds:
                                client.outputStream.writeByte(ConnFlags.SendIds.getAsByte());
                                client.outputStream.writeInt(client.id);
                                client.outputStream.flush();
                                break;
                            case CloseConnection:
                                // break;
                            case Invalid:
                            default:
                                break;
                                // throw new Exception("Invalid flag");
                        }
                    } catch (Exception e) {
                        System.err.println("[!!Error]> Receiving message: " + e.getMessage());
                        e.printStackTrace();
                        client.close();
                        clients.remove(client);
                        break;
                    }
                }
            });
            client.tmpThread.start();
            client.listening = true;
        }
    }

    private void syncPlayerState(ClientItem client) throws Exception {
        Point2D pos = new Point2D(client.inputStream.readFloat(), client.inputStream.readFloat());
        for (ClientItem c : clients) {
            if (c.id == client.id)
                continue;
            c.outputStream.writeByte(ConnFlags.SyncPlayer.getAsByte());
            c.outputStream.writeInt(c.id);
            c.outputStream.writeFloat((float) pos.getX());
            c.outputStream.writeFloat((float) pos.getY());
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
            for (ClientItem client : clients) {
                client.outputStream.writeByte(flag.getAsByte());
                client.outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[!!Error]> Sending message: " + e.getMessage());
            closeEverything();
        }
    }

    public void startGame() {
        ConnFlags flag = ConnFlags.StartGame;
        try {
            for (ClientItem client : clients) {
                client.outputStream.writeByte(flag.getAsByte());
                client.outputStream.writeInt(clients.size());
                client.outputStream.flush();
            }
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

    public void closeEverything() {
        try {
            for (ClientItem client : clients) {
                client.outputStream.writeByte(ConnFlags.CloseConnection.getAsByte());
                client.outputStream.flush();
                client.close();
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

    public void interruptClients() {
        for (ClientItem client : clients) {
            if (client.tmpThread != null)
                client.tmpThread.interrupt();
        }
    }

    public void interruptConnection() {
        if (connectionThread != null)
            connectionThread.interrupt();
    }

    public ArrayList<ClientItem> getClients() {
        return clients;
    }
}
