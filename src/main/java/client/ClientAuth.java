package main.java.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static main.java.client.ChatClient.socket;

public class ClientAuth {

    private static BufferedReader in;
    private static PrintWriter out;

    private Socket clientSocket;

    protected ClientAuth(Socket socket) throws IOException {
        clientSocket = socket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public static boolean isRegisteredUser(String username) {
        String outstring = String.format("isregistered:%s", username);
        out.println(outstring);

    }

    public static boolean registerUser(String username, String password) {
    }
}
