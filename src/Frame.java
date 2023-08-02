import javax.swing.*;

public class Frame {

    JFrame Frame;

    public Frame(JFrame Frame) {
        this.Frame = Frame;
    }

    public static void main(String[] args){
        JFrame meinFrame = new JFrame("Frame");
        meinFrame.setSize(200,200);


        var volleKasten = JOptionPane.showInputDialog(null ,"Wie viele volle Kästen sind da?");
        System.out.println(volleKasten);

        meinFrame.setVisible(true);
    }
}
