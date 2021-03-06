import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Driver
{
    public static void main(String[] args)
    {
        Dotenv dotenv = Dotenv.load();
        String API_KEY = dotenv.get("CALORIE_COUNTER_API_KEY");
        String API_ID = dotenv.get("CALORIE_COUNTER_API_ID");
        Calories counter = new Calories();
        Map<String, Double> map = new HashMap<>();

        //GUI
        Border border = BorderFactory.createLineBorder(Color.BLACK);

        //Frame
        JFrame frame = new JFrame();
        frame.setSize(800, 400);
        frame.setResizable(false);

        //Panels
        JPanel masterPanel = new JPanel(new BorderLayout());
        JPanel gridLeft = new JPanel(new FlowLayout());
        JPanel gridRight = new JPanel(new BorderLayout());

        masterPanel.add(gridLeft);
        masterPanel.add(gridRight, BorderLayout.EAST);
        masterPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        //Create text field and label for user input
        JLabel label = new JLabel("Please enter food item:");
        JTextField foodTextField = new JTextField();
        foodTextField.setBorder(border);
        foodTextField.setPreferredSize(new Dimension(150,40));
        gridLeft.add(label);
        gridLeft.add(foodTextField);

        //Create label to display total calories
        JLabel resultLabel = new JLabel();
        JLabel totalLabel = new JLabel();
        gridLeft.add(resultLabel);

        //Create submit button
        JButton submitBtn = new JButton("Add Item");
        submitBtn.setPreferredSize(new Dimension(90, 40));

        //Create table to display food item and calories
        String [] columnNames = {"Food" , "Calories"};
        DefaultTableModel defaultModel = new DefaultTableModel(columnNames, 0);
        JTable foodTable = new JTable(defaultModel);
        foodTable.setBackground(Color.WHITE);

        //Add table to scroll pane
        gridRight.add(new JScrollPane(foodTable));

        submitBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Get user input
                String userInput = foodTextField.getText();
                if(!userInput.equals(""))
                {
                    //Add food item and calories to array of items
                    double calories = queryAPI(API_KEY, API_ID, userInput);
                    if (calories != -1)
                    {
                        //If table already contains item add calories to existing item
                        if(map.containsKey(userInput))
                        {
                            map.put(userInput, map.get(userInput) + calories);
                        }
                        else map.put(userInput, calories);

                        //Add food item calories to total
                        counter.addCalories(calories);

                        resultLabel.setText("");
                    } else resultLabel.setText("Unable to find item");


                    //Empty text box
                    foodTextField.setText("");

                    //Update table that displays food items and calories
                    defaultModel.setRowCount(0);
                    for (Map.Entry<String, Double> entry : map.entrySet()) {
                        defaultModel.addRow(new Object[] {entry.getKey(), Math.floor(entry.getValue() * 100) / 100});
                    }

                    gridLeft.add(totalLabel);

                    //Display total calories
                    totalLabel.setText("Total Calories: " + Math.floor(counter.getCalories() * 100) / 100);

                    frame.validate();
                }
                else
                {
                    resultLabel.setText("Please enter in a valid item");
                }
            }});
        gridLeft.add(submitBtn);

        frame.add(masterPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Calorie Tracker");
        frame.setVisible(true);
        //frame.pack();
        frame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static double queryAPI(String API_KEY, String API_ID, String userInput)
    {
        try
        {
            //Check if food item has any empty spaces and remove them
            String foodItem = userInput.replaceAll("\\s+", "");

            //API
            String query = "https://api.nutritionix.com/v1_1/search/" + foodItem + "?results=0:20&fields=nf_calories&" +
                    "appId=" + API_ID + "&appKey=" + API_KEY + "";

            //Create API request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(query))
                    .build();

            //Get API response
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode foodCal = Parser.parse(response.body()).get("hits").get(0).get("fields").get("nf_calories");

            return foodCal.asDouble();
        }
        catch(IOException | InterruptedException e)
        {
            System.out.println("Unable to find item");
            return -1;
        }
    }
}
