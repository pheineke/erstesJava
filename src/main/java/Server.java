package main.java;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private static final int PORT = 3344;
    private static final int MAX_CONNECTIONS = 10;
    private static final HashMap<String, String> userMap = new HashMap<>();
    private static final HashMap<String, String> previousUserMap = new HashMap<>();
    private static final ArrayList<Socket> socketlist = new ArrayList<>();
    private static final AuthenticationManager authManager = new AuthenticationManager();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            if (socketlist.size() < MAX_CONNECTIONS) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String usersocket;

        public ClientHandler(Socket socket) throws IOException {
            clientSocket = socket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("chatauth")) {
                        String[] authInfo = inputLine.split("\\|");
                        if (authInfo.length == 3) {
                            String username = authInfo[1].trim();
                            String password = authInfo[2];
                            handleLogin(clientSocket, username, password);
                        }
                    } else {
                        if (authManager.isLoggedIn(usersocket)) {
                            String user = userMap.get(usersocket);
                            if (user != null) {
                                System.out.println(user + ": " + inputLine);
                                // Sende die Nachricht an alle Clients
                                for (Socket socket : socketlist) {
                                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                                    writer.println(user + ": " + inputLine);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e);
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                    socketlist.remove(clientSocket);

                    String user = userMap.get(usersocket);
                    if (user != null) {
                        // Sende eine Nachricht an alle Clients, dass dieser Benutzer den Chat verlassen hat.
                        for (Socket socket : socketlist) {
                            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                            writer.println(user + " has left the chat.");
                        }
                    }

                    // Entferne den Benutzer aus der Benutzerzuordnung
                    userMap.remove(usersocket);
                    previousUserMap.remove(usersocket);
                    authManager.logout(usersocket);
                } catch (IOException e) {
                    System.err.println("Error closing client connection: " + e);
                }
            }
        }

        private void handleLogin(Socket clientSocket, String username, String password) throws IOException {
            usersocket = extractIPAddress(clientSocket.toString());

            if (!authManager.isLoggedIn(usersocket)) {
                if (authManager.authenticate(username, password)) {
                    String prevUser = previousUserMap.get(usersocket); // Vorheriger Benutzername
                    if (prevUser != null) {
                        // Sende eine Nachricht an alle Clients, dass der vorherige Benutzer den Chat verlassen hat
                        for (Socket socket : socketlist) {
                            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                            writer.println(prevUser + " (previously \"" + prevUser + "\")" + " has left the chat.");
                        }
                    }

                    // Schritt 3: Join-Nachricht beim Verbinden
                    authManager.login(usersocket);
                    userMap.put(usersocket, username);
                    socketlist.add(clientSocket);

                    // Sende den Benutzernamen an alle Clients
                    for (Socket socket : socketlist) {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println(username + " has joined the chat.");
                    }

                    System.out.println(usersocket);
                    System.out.println(username + " " + usersocket + " connected");

                    // Speichere den aktuellen Benutzernamen als vorherigen Benutzernamen für diese IP
                    previousUserMap.put(usersocket, username);
                } else {
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    writer.println("Authentication failed. Please check your username and password.");
                    clientSocket.close();
                }
            } else {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println("You are already logged in.");
                clientSocket.close();
            }
        }
    }

    public static String extractIPAddress(String socketString) {
        // Finde den Startindex der IP-Adresse im String
        int startIndex = socketString.indexOf("/");

        // Finde den Endindex der IP-Adresse im String
        int endIndex = socketString.indexOf(",", startIndex);

        // Extrahiere die IP-Adresse aus dem String
        String ipAddress = socketString.substring(startIndex + 1, endIndex);

        return ipAddress;
    }
}
