
import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    Classifier classifier;

    public GUI(Classifier classifier){

        this.classifier = classifier;
    }

    public void showGUI(){

        setTitle("Classifier");

        JPanel panel = new JPanel();

        panel.setPreferredSize(new Dimension(600,400));

        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel("Write text");

        label.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField textField = new JTextField("Write text here");

        JButton button = new JButton("Start!");

        button.addActionListener((o) ->{

            if(o.getActionCommand().equals("Start!")) {

             label.setText(classifier.testing_console(textField.getText()));

            }
        });

        panel.add(label,BorderLayout.PAGE_START);
        panel.add(textField,BorderLayout.CENTER);
        panel.add(button,BorderLayout.PAGE_END);


        add(panel);
        this.pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);


    }
}
