import javax.swing.*;
import java.awt.*;

public class Frame {

    JFrame Frame = new JFrame("Chat");

    public Frame(JFrame Frame) {
        this.Frame = Frame;
    }

    public static void main(String[] args){
        JFrame meinFrame = new JFrame("Frame");
        meinFrame.setSize(200,200);

        //output(meinFrame, );


        var volleKasten = JOptionPane.showInputDialog(null ,"Wie viele volle Kästen sind da?");
        System.out.println(volleKasten);

        meinFrame.setVisible(true);
    }

    public static void output(JFrame frame, String input) {
        frame.add(new JLabel(input, SwingConstants.CENTER));

    }
}
