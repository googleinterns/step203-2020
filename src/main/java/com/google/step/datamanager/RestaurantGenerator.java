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

  public static final String API_KEY = readMapApiKey();
  public static final String FAKE_API_KEY = "api-key";

  private static final String SEARCH_URL =
      "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
  private static final String LOCATION = "1.352,103.8198";
  private static final String RADIUS = "13000";
  private static final String TYPE = "restaurant";

  private static final String FILE_NAME = "restaurants.txt";
  private static final RestaurantManager restaurantManager = new RestaurantManagerDatastore();

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

  private static void createRestaurants(JsonArray restaurantsJsonArray) {

    for (JsonElement restaurantElement : restaurantsJsonArray) {
      JsonObject restaurantObject = restaurantElement.getAsJsonObject();
      String name = restaurantObject.get("name").getAsString();
      JsonElement photo = restaurantObject.get("photos").getAsJsonArray().get(0);
      String photoReference = photo.getAsJsonObject().get("photo_reference").getAsString();

      restaurantManager.createRestaurantWithPhotoReference(name, photoReference, -1);
    }
  }

  /**
   * Returns the map api key read from the json file.
   *
   * @return the map api key.
   */
  private static String readMapApiKey() {
    String content;
    try {
      File file = new File(RestaurantGenerator.class.getResource("/api-key.json").getFile());
      content = new String(Files.readAllBytes(file.toPath()));
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
      return FAKE_API_KEY;
    }

    JsonObject jsonObject;
    try {
      jsonObject = JsonParser.parseString(content).getAsJsonObject();
      return jsonObject.get("map-api-key").getAsString();
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return FAKE_API_KEY;
    }
  }
}
