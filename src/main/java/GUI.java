import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI implements ActionListener
{
    String userInput;
    JLabel resultLabel;
    JTextField foodTextField;

    public GUI()
    {
        userInput = "";
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));

        JLabel label = new JLabel("Calorie Tracker");
        panel.add(label);

        foodTextField = new JTextField();
        panel.add(foodTextField);

        JButton button = new JButton("Add Item");
        button.addActionListener(this);
        panel.add(button);

        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Calorie Tracker");
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(!userInput.equals(""))
        {
            userInput = foodTextField.getText();
            System.out.println(userInput);
        }
        else
        {

        }
    }

    public String getUserInput()
    {
        if(!userInput.equals(""))
        {
            return userInput;
        }
        else
        {
            //FIX
            return "";
        }
    }

    public void displayCalories(double result)
    {
        resultLabel.setText(result + "");
    }

    public void displayError()
    {
        resultLabel.setText("Unable to find item");
    }


}
