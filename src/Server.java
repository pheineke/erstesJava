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
        private String username;

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
                    String usersocket = extractIPAddress(clientSocket.toString());

                    if (!inputLine.contains("chatauth")) {
                        if (socketlist.contains(clientSocket)) {
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
