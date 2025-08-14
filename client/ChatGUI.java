package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private ChatClient chatClient;

    public ChatGUI(String username, boolean isAdmin) {
        setTitle("💬 Chat - " + username + (isAdmin ? " (Admin)" : ""));
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        ActionListener sendAction = e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                chatClient.sendMessage(message);
                inputField.setText("");
            }
        };

        inputField.addActionListener(sendAction);
        sendButton.addActionListener(sendAction);

        try {
            chatClient = new ChatClient("localhost", 12345, username, isAdmin, this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Unable to connect to server.");
            System.exit(1);
        }

        setVisible(true);
    }

    public void showMessage(String message) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        chatArea.append("[" + timeStamp + "] " + message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
