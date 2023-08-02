import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3344;
    private static final int MAX_MESSAGE_LENGTH = 100;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    private static JTextArea chatArea;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Color chatBackgroundColor = DEFAULT_BACKGROUND_COLOR;
    private static String username;

    public static void main(String[] args) {
        chatinitJF();
    }

    public static void chatinitJF() {
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

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
                    if (chatArea != null) { // Hier prüfen wir, ob chatArea nicht null ist
                        chatArea.setBackground(chatBackgroundColor);
                    }
                }
            }
        });

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = userField.getText();
                String hostip = hostField.getText();
                int hostport;

                try {
                    hostport = Integer.parseInt(portField.getText());
                } catch (NumberFormatException ex) {
                    hostport = DEFAULT_PORT;
                }

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        chat(hostip, hostport);

                        // Schließe das "Login"-Fenster, nachdem die Verbindung erfolgreich hergestellt wurde
                        frame.dispose();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error connecting to server", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(userLabel);
        panel.add(userField);
        panel.add(hostLabel);
        panel.add(hostField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(colorLabel);
        panel.add(colorButton);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(connectButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void chat(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        out.println("chatauth" + username);

        JFrame chatFrame = new JFrame("Chat - " + username);
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(500, 500);

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

        ServerListener serverListener = new ServerListener();
        Thread listenerThread = new Thread(serverListener);
        listenerThread.start();
    }

    private static class ServerListener implements Runnable {
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
}
