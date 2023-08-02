import java.io.*;
import javax.swing.*;

public class ConsoleOutput extends OutputStream {
    private JTextArea textArea;

    public ConsoleOutput(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        textArea.append(String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public static void main() {
        JTextArea textArea = new JTextArea(20, 120);
        JScrollPane scrollPane = new JScrollPane(textArea);
        JFrame frame = new JFrame("Console Output");
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setVisible(true);

        ConsoleOutput consoleOutput = new ConsoleOutput(textArea);
        System.setOut(new PrintStream(consoleOutput));
        System.setErr(new PrintStream(consoleOutput));

        System.out.println("Hello, world!");
    }
}
