import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Stack;

public class Server {
    int usersize = 10;
    static int counter = 0;
    static final ArrayList userlist = new ArrayList();
    static final ArrayList socketlist = new ArrayList();
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(3344);
        System.out.println("Server started");

        Socket clientSocket = serverSocket.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            String usersocket0 = clientSocket.toString().substring(inputLine.indexOf("Socket[addr=/") + 14 , clientSocket.toString().length());
            String usersocket = usersocket0.split("\\,", 2)[0];


            if (!inputLine.contains("chatauth")) {
                if (socketlist.contains(usersocket)) {
                    String user = (String) userlist.get(socketlist.indexOf(usersocket));
                    System.out.println(user);
                    System.out.println(user + ": " + inputLine);
                    out.println(user + ": " + inputLine);
                }
            }
            if (inputLine.contains("chatauth")) {
                String user = inputLine.substring(inputLine.indexOf("chatauth") + 8 , inputLine.length());
                userlist.add(user);
                socketlist.add(usersocket);
                System.out.println(user + " " + usersocket + " connected");
            }
        }
    }
}