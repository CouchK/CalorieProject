import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Driver
{
    public static void main(String[] args)
    {
        Dotenv dotenv = Dotenv.load();
        String APP_KEY = dotenv.get("CALORIE_COUNTER_API_KEY");
        String APP_ID = dotenv.get("CALORIE_COUNTER_API_ID");
        String userInput, foodItem, cont;
        Calories counter = new Calories();
        Scanner input = new Scanner(System.in);
        JsonNode foodCal = null;
        boolean complete = false;
        double calories = 0.0;
        Map<String, Double> map = new HashMap<>();

        while (!complete) {
            //User input
            System.out.println("Please enter in a food item to add: ");
            userInput = input.nextLine();

            //Check if food item has any empty spaces and remove them
            foodItem = userInput.replaceAll("\\s+", "");

            //API
            String query = "https://api.nutritionix.com/v1_1/search/" + foodItem + "?results=0:20&fields=nf_calories&" +
                    "appId=" + APP_ID + "&appKey=" + APP_KEY + "";

            //Create API request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(query))
                    .build();

            //Get API response
            try
            {
                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response != null) {

                    foodCal = Parser.parse(response.body()).get("hits").get(0).get("fields").get("nf_calories");
                    System.out.println(foodCal + " Calories");
                    calories = foodCal.asDouble();

                    //Add food item and calories to array of items
                    map.put(userInput, calories);

                    //Add food item calories to total
                    counter.addCalories(calories);
                }
            } catch (Exception e)
            {
                System.out.println("Unable to find item");
            }

            //Ask if complete or continue
            System.out.println("Would you like to add another item? Y/N");
            cont = input.nextLine();
            if (cont.equals("Y") || cont.equals("y")) continue;
            else complete = true;
        }

        //Print out all food items and calories
        map.forEach((key, value) -> System.out.println(key + " -> " + value));

        //Display total calories
        System.out.println("======================== \nTotal Calories: " + counter.getCalories());
    }
}
