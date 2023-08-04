package main.java.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    protected static final int PORT = 3344;
    protected static final int MAX_CONNECTIONS = 10;
    protected static final HashMap<String, String> userMap = new HashMap<>();
    protected static final HashMap<String, String> previousUserMap = new HashMap<>();
    protected static final ArrayList<Socket> socketlist = new ArrayList<>();
    protected static final AuthenticationManager authManager = new AuthenticationManager();

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
