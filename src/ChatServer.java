import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ChatServer {
    static ArrayList userlist = new ArrayList();
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(3344);
        System.out.println("Server started");

        Socket clientSocket = serverSocket.accept();
        System.out.println(userlist.get(userlist.size()-1).toString() + " connected");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println(clientSocket.toString() + inputLine);
            out.println("Server: " + inputLine);
        }
    }

    public static void clientconnect(String user) {
        userlist.add(user);
    }
}