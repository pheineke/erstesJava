import javax.swing.*;
import java.awt.*;

public class Frame {

    public static void main(String[] args){
        JFrame meinFrame = new JFrame("Frame");

        meinFrame.setSize(200,200);

        meinFrame.add(new JLabel("Wie viele volle Kästen sind da?", SwingConstants.CENTER));

        var volleKasten = JOptionPane.showInputDialog(null ,"Wie viele volle Kästen sind da?");
        System.out.println(volleKasten);

        Color color = null;
        switch (volleKasten) {
            case "white" -> color = Color.WHITE;
            case "black" -> color = Color.BLACK;
            case "red" -> color = Color.RED;
            case "blue" -> color = Color.RED;
            case "green" -> color = Color.GREEN;
            case "yellow" -> color = Color.YELLOW;
            case "orange" -> color = Color.ORANGE;
            case "gray" -> color = Color.GRAY;
            case "pink" -> color = Color.PINK;
            case "cyan" -> color = Color.CYAN;
            default -> meinFrame.add(new JLabel("Eingabe Ungültig.", SwingConstants.CENTER));
        }

        meinFrame.getContentPane().setBackground(color);



        meinFrame.setVisible(true);
    }
}
