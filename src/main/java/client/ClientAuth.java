package main.java.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAuth {

    private static final int AUTH_PORT = ChatClient.PORT+1;
    private static final String HOST = ChatClient.HOST;
    private static BufferedReader in;
    private static PrintWriter out;

    private static Socket clientSocket;

    public static void authsocket() throws IOException {
        clientSocket = new Socket(HOST, AUTH_PORT);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public static boolean isRegisteredUser(String username) throws IOException {
        authsocket();
        String outstring = String.format("isregistered:%s", username);
        String trueinstring = String.format("trueregistered:%s");
        String falseinstring = String.format("falseregistered:%s");

        out.println(outstring);

        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Aktualisiere die Benutzeroberfläche des Clients mit der empfangenen Nachricht
                if (in.readLine().equals(trueinstring)) {
                    return true;
                } else if (in.readLine().equals(falseinstring)) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean registerUser(String username, String password) throws IOException {
        authsocket();
        String outstring = String.format("register:%s:%s", username,password);
        String trueinstring = String.format("trueregister:%s");
        String falseinstring = String.format("falseregister:%s");

        out.println(outstring);

        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Aktualisiere die Benutzeroberfläche des Clients mit der empfangenen Nachricht
                if (in.readLine().equals(trueinstring)) {
                    return true;
                } else if (in.readLine().equals(falseinstring)) {
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
