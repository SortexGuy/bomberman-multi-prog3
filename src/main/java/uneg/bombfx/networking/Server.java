package uneg.bombfx.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.scene.layout.VBox;

import uneg.bombfx.views.MainGameController;

/**
 * Server
 */
public class Server {
    private ServerSocket sSocket;
    private Socket socket;
    private BufferedReader bReader;
    private BufferedWriter bWriter;

    public Server(ServerSocket sSocket) {
        this.sSocket = sSocket;
        try {
            this.socket = this.sSocket.accept();
            this.bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in ServerSocket: " + e.getMessage());
            closeEverything(socket, bReader, bWriter);
        }
    }

    public void closeEverything(Socket socket, BufferedReader bReader, BufferedWriter bWriter) {
        try {
            if (bReader != null) {
                bReader.close();
            }
            if (bWriter != null) {
                bWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error closing everything: " + e.getMessage());
        }
    }

    public void receiveMessage(VBox textBox) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String message = bReader.readLine();
                        MainGameController.addLabel(message, textBox);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error receiving message: " + e.getMessage());
                        closeEverything(socket, bReader, bWriter);
                        break;
                    }
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        try {
            bWriter.write(message);
            bWriter.newLine();
            bWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending message: " + e.getMessage());
            closeEverything(socket, bReader, bWriter);
        }
    }
}
