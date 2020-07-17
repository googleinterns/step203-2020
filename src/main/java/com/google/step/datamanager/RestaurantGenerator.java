package com.google.step.datamanager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

/** A class that handles populating restaurants database. */
public class RestaurantGenerator {

  private static final String SEARCH_URL =
      "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
  private static final String API_KEY = "AIzaSyAmdO6DpMLWi4ZdW6nHgvmQF9zDNiY3k28";
  private static final String LOCATION = "1.352,103.8198";
  private static final String RADIUS = "13000";
  private static final String TYPE = "restaurant";

  private static final String FILE_NAME = "restaurants.txt";

  /** Fetches restaurants info and writes to a file. */
  private static void generateRestaurantsJsonFile() {
    HttpURLConnection connection = null;
    String searchUrl =
        SEARCH_URL
            + "?"
            + "key="
            + API_KEY
            + "&location="
            + LOCATION
            + "&radius="
            + RADIUS
            + "&type="
            + TYPE;
    StringBuilder jsonResults = new StringBuilder();

    try {

      URL url = new URL(searchUrl);
      connection = (HttpURLConnection) url.openConnection();
      InputStreamReader in = new InputStreamReader(connection.getInputStream());

      int read;
      char[] buff = new char[1024];
      while ((read = in.read(buff)) != -1) {
        jsonResults.append(buff, 0, read);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

    JsonObject jsonObject;
    try {
      jsonObject = JsonParser.parseString(jsonResults.toString()).getAsJsonObject();
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return;
    }

    writeToFile(jsonObject.toString());
  }

  private static void writeToFile(String content) {
    try {
      FileWriter fileWriter = new FileWriter("src/main/resources/" + FILE_NAME);
      fileWriter.write(content);
      fileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Populates restaurant database. */
  public static void populateRestaurantsDatabase() {
    File file = new File(RestaurantGenerator.class.getResource("/restaurants.txt").getFile());
    String content;
    try {
      content = new String(Files.readAllBytes(file.toPath()));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    JsonObject jsonObject;
    try {
      jsonObject = JsonParser.parseString(content).getAsJsonObject();
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return;
    }
    createRestaurants(jsonObject.get("results").getAsJsonArray());
  }

  private static void createRestaurants(JsonArray restaurantsJsonArray) {
    RestaurantManager restaurantManager = new RestaurantManagerDatastore();
    for (JsonElement restaurantElement : restaurantsJsonArray) {
      JsonObject restaurantObject = restaurantElement.getAsJsonObject();
      String name = restaurantObject.get("name").getAsString();
      restaurantManager.createRestaurant(name, "test"); // TODO
    }
  }
}
