import java.io.*;
import java.net.*;

import javax.swing.*;

public class ChatClient {

    static String hostip = "localhost";
    static int hostport = 0000;

    public static void main(String[] args) throws IOException {
        chatinit();
        chat(hostip, hostport);

    }

    public static void chat(String host, int port) throws IOException {
        Socket socket = new Socket("localhost", 3344);
        System.out.println("Connected to server");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            System.out.println(/*"Server: " + */ in.readLine());
        }
    }

    public static void chatinit() {
        JPanel chatinitpanel = new JPanel();
        JTextField ipfield = new JTextField(15);
        JTextField portfield = new JTextField(6);

        chatinitpanel.add(new JLabel("IP:"));   chatinitpanel.add(ipfield);
        chatinitpanel.add(Box.createHorizontalStrut(15)); // a spacer
        chatinitpanel.add(new JLabel("Port:"));    chatinitpanel.add(portfield);

        var result = JOptionPane.showConfirmDialog(null, chatinitpanel, "ChatInit", JOptionPane.OK_CANCEL_OPTION);
        System.out.println(ipfield.getText() +" "+ portfield.getText());

        hostip = ipfield.getText();
        hostport = Integer.valueOf(portfield.getText());

        //hostip = JOptionPane.showInputDialog(null, "Auf welche IP möchtest du dich verbinden?");
        //hostport = JOptionPane.showInputDialog(null ,"Welcher Port?"));
    }
}