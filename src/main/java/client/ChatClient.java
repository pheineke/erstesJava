package main.java.client;

import main.java.server.Authentication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3344;
    private static String HOST = DEFAULT_HOST;
    private static int PORT = DEFAULT_PORT;
    private static final int MAX_MESSAGE_LENGTH = 100;


    protected static Socket socket;

    private static BufferedReader in;
    private static PrintWriter out;
    private static String username;
    private static String password;


    private static JTextArea chatArea;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static Color chatBackgroundColor = DEFAULT_BACKGROUND_COLOR;



    public static void main(String[] args) {
        chatInitJF();
    }

    public static void chatInitJF() {
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JLabel hostLabel = new JLabel("Host:");
        JTextField hostField = new JTextField(20);
        hostField.setText(DEFAULT_HOST);

        JLabel portLabel = new JLabel("Port:");
        JTextField portField = new JTextField(20);
        portField.setText(Integer.toString(DEFAULT_PORT));

        JLabel colorLabel = new JLabel("Chat Background Color:");
        JButton colorButton = new JButton("Select");
        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(frame, "Select Chat Background Color", chatBackgroundColor);
                if (selectedColor != null) {
                    chatBackgroundColor = selectedColor;
                    if (chatArea != null) {
                        chatArea.setBackground(chatBackgroundColor);
                    }
                }
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = userField.getText();
                password = new String(passwordField.getPassword());

                try {
                    username = userField.getText();
                    password = passwordField.getText();
                } catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a username and password", "Error", JOptionPane.INFORMATION_MESSAGE);
                }

                if (Authentication.registerUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Registration successful! Please login with the same credentials.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Username is already in use. Please choose a different username.", "Registration Failed", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = userField.getText();
                password = new String(passwordField.getPassword());
                String hostip = hostField.getText();

                try {
                    HOST = hostField.getText();
                } catch (NullPointerException ex) {
                    ;
                }

                try {
                    username = userField.getText();
                    password = new String(passwordField.getPassword());
                } catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a username and password", "Error", JOptionPane.INFORMATION_MESSAGE);
                }

                try {
                    PORT = Integer.parseInt(portField.getText());
                } catch (NumberFormatException ex) {
                    PORT = DEFAULT_PORT;
                }

                if (ClientAuth.isRegisteredUser(username)) {
                    try {
                        frame.dispose();
                        chat(hostip, PORT, username, password);

                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Cannot connect to Server.", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "There is no registered User " + username + ".", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });


        panel.add(userLabel);
        panel.add(userField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(hostLabel);
        panel.add(hostField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(colorLabel);
        panel.add(colorButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerButton);
        buttonPanel.add(connectButton);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void chat(String host, int port, String username, String password) throws IOException {
        // Überprüfen, ob der Benutzer bereits registriert ist
        if (!Authentication.isRegisteredUser(username)) {
            JOptionPane.showMessageDialog(null, "Please register with the given username and password", "Registration Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String storedPassword = Authentication.getPasswordForUser(username);
        if (!password.equals(storedPassword)) {
            JOptionPane.showMessageDialog(null, "Invalid credentials. Please check your username and password", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Socket socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        out.println("chatauth" + "|" + username + "|" + password);

        JFrame chatFrame = new JFrame("Chat - " + username);
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea(20, 20);
        chatArea.setEditable(false);
        chatArea.setBackground(chatBackgroundColor);

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JTextField messageField = new JTextField(MAX_MESSAGE_LENGTH);
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    out.println(message);
                    messageField.setText("");
                }
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.isEmpty()) {
                    out.println(message);
                    messageField.setText("");
                }
            }
        });

        chatPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        chatFrame.getContentPane().add(chatPanel);

        chatFrame.pack();
        chatFrame.setVisible(true);

        ServerListener serverListener = new ServerListener(in);
        Thread listenerThread = new Thread(serverListener);
        listenerThread.start();
    }


    private static class ServerListener implements Runnable {
        private BufferedReader in;

        public ServerListener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // Aktualisiere die Benutzeroberfläche des Clients mit der empfangenen Nachricht
                    String finalInputLine = inputLine;
                    SwingUtilities.invokeLater(() -> chatArea.append(finalInputLine + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isRegisteredUser(String username) throws Exception {
        Socket socket = new Socket(HOST, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        return true;
    }
    private boolean isRightPassword(String username, String password) {
        Socket socket = new Socket(HOST, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        return true;
    }
}