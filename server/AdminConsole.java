package server;

import java.util.Scanner;

public class AdminConsole implements Runnable {

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String command = scanner.nextLine().trim();

            if (command.equalsIgnoreCase("/list")) {
                System.out.println("📋 Active users:");
                synchronized (ChatServer.clientWriters) {
                    for (String user : ChatServer.clientWriters.keySet()) {
                        System.out.println("• " + user);
                    }
                }
            } else if (command.startsWith("/kick ")) {
                String userToKick = command.substring(6).trim();
                if (ChatServer.kickUser(userToKick)) {
                    System.out.println("✅ User " + userToKick + " was kicked.");
                } else {
                    System.out.println("❌ Could not kick user: " + userToKick);
                }
            } else if (command.startsWith("/announce ")) {
                String announcement = "[Admin Announcement]: " + command.substring(10).trim();
                ChatServer.broadcastMessage(announcement);
            } else if (command.equalsIgnoreCase("/shutdown")) {
                System.out.println("⚠️ Shutting down server...");
                ChatServer.shutdownServer();
                break;
            } else {
                System.out.println("❓ Unknown command. Try: /list, /kick <user>, /announce <msg>, /shutdown");
            }
        }

        scanner.close();
    }
}

