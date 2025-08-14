package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader input;
    private final PrintWriter output;
    private final List<ClientHandler> clients;
    private String username;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;

        BufferedReader tempIn = null;
        PrintWriter tempOut = null;
        try {
            tempIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            tempOut = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        input = tempIn;
        output = tempOut;
    }

    @Override
    public void run() {
        try {
            output.println("Enter your username:");
            username = input.readLine();
            broadcast("🔔 " + username + " has joined the chat");

            String message;
            while ((message = input.readLine()) != null) {
                broadcast(username + ": " + message);
            }
        } catch (IOException e) {
            System.out.println(username + " disconnected.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
            broadcast("❌ " + username + " has left the chat");
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.output.println(message);
        }
    }
}
