package client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ChatGUI gui;

    public ChatClient(String serverAddress, int port, String username, boolean isAdmin, ChatGUI gui) throws IOException {
        this.gui = gui;
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        out.println(username + (isAdmin ? "::admin" : ""));

        new Thread(() -> {
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    gui.showMessage(line);
                }
            } catch (IOException e) {
                gui.showMessage("❌ Connection closed.");
            }
        }).start();
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
