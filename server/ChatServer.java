package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    public static final int PORT = 12345;
    public static Map<String, PrintWriter> clientWriters = new HashMap<>();
    public static Map<String, Socket> clientSockets = new HashMap<>();

    private static ServerSocket serverSocket;

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Chat server started on port " + PORT + "...");
        serverSocket = new ServerSocket(PORT);

        // Start admin console
        new Thread(new AdminConsole()).start();

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start();
        }
    }

    public static void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(message);
            }
        }
    }

    public static boolean kickUser(String username) {
        Socket socket = clientSockets.get(username);
        if (socket != null) {
            try {
                PrintWriter out = clientWriters.get(username);
                out.println("❌ You have been kicked by the admin.");
                socket.close(); // disconnect
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    public static void shutdownServer() {
        broadcastMessage("⚠️ Server is shutting down. All clients will be disconnected.");

        synchronized (clientSockets) {
            for (Socket socket : clientSockets.values()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server socket.");
        }

        System.exit(0);
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private String username;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine();

                synchronized (clientWriters) {
                    if (clientWriters.containsKey(username)) {
                        out.println("❌ Username already taken.");
                        socket.close();
                        return;
                    }

                    clientWriters.put(username, out);
                    clientSockets.put(username, socket);
                }

                broadcastMessage("🔔 " + username + " has joined the chat");

                String input;
                while ((input = in.readLine()) != null) {
                    if (input.startsWith("/msg ")) {
                        String[] parts = input.split(" ", 3);
                        if (parts.length >= 3) {
                            String recipient = parts[1];
                            String privateMsg = "[Private] " + username + ": " + parts[2];

                            PrintWriter recipientWriter = clientWriters.get(recipient);
                            if (recipientWriter != null) {
                                recipientWriter.println(privateMsg);
                                out.println(privateMsg);
                            } else {
                                out.println("❌ User " + recipient + " not found.");
                            }
                        } else {
                            out.println("❌ Invalid private message format. Use: /msg <user> <message>");
                        }
                    } else {
                        broadcastMessage(username + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println("⚠️ Connection error with " + username);
            } finally {
                if (username != null) {
                    synchronized (clientWriters) {
                        clientWriters.remove(username);
                        clientSockets.remove(username);
                    }
                    broadcastMessage("❌ " + username + " has left the chat");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
