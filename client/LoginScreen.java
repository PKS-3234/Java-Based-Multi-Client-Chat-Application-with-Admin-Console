package client;

import javax.swing.*;
import java.awt.event.*;

public class LoginScreen extends JFrame {
    public LoginScreen() {
        setTitle("Login");
        setSize(300, 180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Enter your username:");
        JTextField usernameField = new JTextField(15);
        JCheckBox adminCheckbox = new JCheckBox("Login as Admin");
        JButton loginButton = new JButton("Join");

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(usernameField);
        panel.add(adminCheckbox);
        panel.add(loginButton);
        add(panel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                boolean isAdmin = adminCheckbox.isSelected();

                if (!username.isEmpty()) {
                    new ChatGUI(username, isAdmin);
                    dispose(); // close login screen
                } else {
                    JOptionPane.showMessageDialog(LoginScreen.this, "Username cannot be empty");
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginScreen();
    }
}
