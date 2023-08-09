package main.java.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import static main.java.server.Server.PORT;

public class ServerAuth {
    private static final int AUTH_PORT = PORT+1;
    private static final int MAX_CONNECTIONS = 10;

    private BufferedReader in;
    private PrintWriter out;
    private static final HashMap<String, String> userMap = new HashMap<>();
    private static final HashMap<String, String> previousUserMap = new HashMap<>();
    private static final ArrayList<Socket> socketlist = new ArrayList<>();
    private static final AuthenticationManager authManager = new AuthenticationManager();

    public static void main(String[] args) throws IOException {
        ServerSocket authSocket = new ServerSocket(AUTH_PORT);
        System.out.println("Server started on port " + AUTH_PORT);

        while (true) {
            if (socketlist.size() < MAX_CONNECTIONS) {
                Socket clientSocket = authSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String usersocket = extractIPAddress(authSocket.toString());

                if (inputLine.contains("chatauth")) {
                    System.out.println(inputLine);
                    String prevUser = previousUserMap.get(usersocket); // Vorheriger Benutzername
                    if (prevUser != null) {
                        // Sende eine Nachricht an alle Clients, dass der vorherige Benutzer den Chat verlassen hat
                        for (Socket socket : socketlist) {
                            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                            writer.println(prevUser + " (previously \"" + prevUser + "\")" + " has left the chat.");
                        }
                    }
                    username = inputLine.substring(inputLine.indexOf("chatauth") + 8);
                    userMap.put(usersocket, username);
                    socketlist.add(clientSocket);
                    System.out.println(usersocket);
                    System.out.println(username + " " + usersocket + " connected");

                    // Sende den Benutzernamen an alle Clients
                    for (Socket socket : socketlist) {
                        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        writer.println(username + " has joined the chat.");
                    }

                    // Speichere den aktuellen Benutzernamen als vorherigen Benutzernamen für diese IP
                    previousUserMap.put(usersocket, username);
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

                // Sende eine Nachricht an alle Clients, dass dieser Benutzer den Chat verlassen hat.
                for (Socket socket : socketlist) {
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    writer.println(username + " has left the chat.");
                }

                // Entferne den Benutzer aus der Benutzerzuordnung
                userMap.remove(extractIPAddress(clientSocket.toString()));
                previousUserMap.remove(extractIPAddress(clientSocket.toString()));
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e);
            }
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
