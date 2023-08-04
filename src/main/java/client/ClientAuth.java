package main.java.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static main.java.client.ChatClient.socket;

public class ClientAuth {

    private BufferedReader in;
    private PrintWriter out;

    private Socket clientSocket = socket;

    protected boolean isRegisteredUser(String username) throws IOException {
        clientSocket = socket;
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);


    }

    public void ClientHandler(Socket socket) throws IOException {

    }
}
