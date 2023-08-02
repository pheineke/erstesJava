import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3344;
    private static final int MAX_MESSAGE_LENGTH = 100;

    private static JTextArea chatArea;

    public static void main(String[] args) {
        chatinitJF();
    }

    public static void chatinitJF() {
        JFrame frame = new JFrame("Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel hostLabel = new JLabel("Host:");
        JTextField hostField = new JTextField(20);
        hostField.setText(DEFAULT_HOST);

        JLabel portLabel = new JLabel("Port:");
        JTextField portField = new JTextField(20);
        portField.setText(Integer.toString(DEFAULT_PORT));

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
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
                        chat(hostip, hostport, username);
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

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().add(connectButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    public static void chat(String host, int port, String username) throws IOException {
        Socket socket = new Socket(host, port);
        System.out.println("Connected to server");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        //CHAT AUTHENTICATION
        String auth = "chatauth" + username.toString();
        out.println(auth);
        //..

        // Create chat area
        chatArea = new JTextArea(20, 50);
        JFrame frame = new JFrame("Chat");
        frame.getContentPane().add(new JScrollPane(chatArea));
        frame.pack();
        frame.setVisible(true);

        // Start server listener thread
        ServerListener serverListener = new ServerListener(in);
        Thread serverThread = new Thread(serverListener);
        serverThread.start();

        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            if (userInput.length() > MAX_MESSAGE_LENGTH) {
                System.out.println("Message too long (max " + MAX_MESSAGE_LENGTH + " characters)");
            } else {
                out.println(userInput);
            }
        }
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
                    chatArea.append(inputLine + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error reading from server: " + e);
            }
        }
    }
}
