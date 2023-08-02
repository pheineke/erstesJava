import java.io.*;
import java.net.*;

import javax.swing.*;

public class ChatClient {

    static String hostip = "localhost";
    static int hostport = 0000;
    static String username = "";

    public static void main(String[] args) throws IOException {
        chatinitJF();

        chat(hostip, hostport);


    }

    public static void chat(String host, int port) throws IOException {
        Socket socket = new Socket("localhost", 3344);
        System.out.println("Connected to server");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        //CHAT AUTHENTICATION
        String auth = "chatauth" + username.toString();
        out.println(auth);
        //..

        String userInput;

        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            System.out.println(/*"Server: " + */ in.readLine());
        }
    }

    public static void chatinitT() throws IOException {
        hostip = terminalinput("IP: ");
        hostport = Integer.valueOf(terminalinput("Port: "));

        username = terminalinput("Username: ");
    }

    public static String terminalinput(String input) throws IOException {
        System.out.println(input);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        // Reading data using readLine
        String name = reader.readLine();

        return name;
    }
    public static void chatinitJF() {
        //JFrame Chatinit
        JPanel chatinitpanel = new JPanel();
        JTextField ipfield = new JTextField(15);
        JTextField portfield = new JTextField(6);
        JTextField usernamefield = new JTextField(15);

        chatinitpanel.add(new JLabel("IP:"));   chatinitpanel.add(ipfield);
        chatinitpanel.add(Box.createHorizontalStrut(15)); // a spacer
        chatinitpanel.add(new JLabel("Port:"));    chatinitpanel.add(portfield);
        chatinitpanel.add(Box.createHorizontalStrut(15));
        chatinitpanel.add(new JLabel("Username:"));    chatinitpanel.add(usernamefield);

        JOptionPane.showConfirmDialog(null, chatinitpanel, "ChatInit", JOptionPane.OK_CANCEL_OPTION);
        System.out.println(ipfield.getText() +" "+ portfield.getText());

        hostip = ipfield.getText();
        hostport = Integer.valueOf(portfield.getText());
        username = usernamefield.getText();

        System.out.println(username);

    }
}