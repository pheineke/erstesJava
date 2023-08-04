package main.java.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static main.java.server.Server.*;

public class ClientHandler implements Runnable {
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
        AuthenticationManager authManager;
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("chatauth")) {
                    String[] authInfo = inputLine.split("\\|");
                    if (authInfo.length == 3) {
                        String username = authInfo[1].trim();
                        String password = authInfo[2];
                        handleLogin(clientSocket, username, password);

                        try {
                            Authentication.isRegistered(username);
                        } catch (NotRegistered ex) {

                        }
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