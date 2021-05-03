import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        Map<String, String> map = new HashMap<>();

        //GUI
        //Frame
        JFrame frame = new JFrame();
        frame.setSize(700, 400);

        //Panels
        JPanel masterPanel = new JPanel(new BorderLayout());
        JPanel gridLeft = new JPanel(new GridLayout(3,2));
        JPanel gridRight = new JPanel(new GridLayout(3,3));

        masterPanel.add(gridLeft, BorderLayout.WEST);
        masterPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        //Create text field and label for user input
        JLabel label = new JLabel("Please enter food item:");
        JTextField foodTextField = new JTextField();
        gridLeft.add(label);
        gridLeft.add(foodTextField);

        //Create label to display total calories
        JLabel resultLabel = new JLabel();
        JLabel totalLabel = new JLabel();
        gridLeft.add(resultLabel);

        //Create submit button
        JButton submitBtn = new JButton("Add Item");

        //Create table to display food item and calories
        String [] columnNames = {"Food" , "Calories"};
        DefaultTableModel defaultModel = new DefaultTableModel(columnNames, 0);
        JTable foodTable = new JTable(defaultModel);
        foodTable.setBackground(Color.WHITE);
        gridRight.add(new JScrollPane(foodTable));

        submitBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                String userInput = foodTextField.getText();
                if(!userInput.equals(""))
                {
                    userInput = foodTextField.getText();

                    //Add food item and calories to array of items
                    double calories = queryAPI(API_KEY, API_ID, userInput);
                    if(calories != -1)
                    {
                        map.put(userInput, String.valueOf(calories));

                        //Add food item calories to total
                        counter.addCalories(calories);

                        resultLabel.setText("");
                    }
                    else resultLabel.setText("Unable to find item");

                    //Empty text box
                    foodTextField.setText("");

                    //Create and update table to display all food items and calories
                    defaultModel.setRowCount(0);
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        defaultModel.addRow(new Object[] {entry.getKey(), entry.getValue()});
                    }

                    gridRight.add(totalLabel);

                    //Display total calories
                    totalLabel.setText("Total Calories: " + counter.getCalories());

                    frame.validate();
                }
                else
                {
                    resultLabel.setText("Please enter in a valid item");
                }
            }});
        gridLeft.add(submitBtn);

        masterPanel.add(gridRight);
        frame.add(masterPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Calorie Tracker");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
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
        catch(Exception e)
        {
            System.out.println("Unable to find item");
            return -1;
        }
    }
}
