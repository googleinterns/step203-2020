package com.google.step.servlets;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.step.model.Deal;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.utils.URIBuilder;

public class DistanceUtil {

  private static Gson gson = new Gson();
  private static final String FAKE_API_KEY = "api-key";
  private static final String API_KEY = readMapApiKey();

  // Class to represent the json object returned from distance matrix api
  private class DistanceResponse {
    private class Row {
      private List<Element> elements;

      private class Element {
        private Distance distance;
        private Duration duration;
        private String status;

        private class Distance {
          String text;
          String value;
        }

        private class Duration {
          String text;
          String value;
        }
      }
    }

    private String status;
    private List<String> origin_addresses;
    private List<String> destination_addresses;
    private List<Row> rows;
  }

  public static List<Map<String, Integer>> getDistances(
      List<Deal> deals, String latitude, String longitude, List<List<String>> placeIdsPerDeal)
      throws IOException {
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
    StringBuilder sb = new StringBuilder();

    // Building url with all the placeids of all the deals
    for (int i = 0; i < placeIdsPerDeal.size(); i++) {
      List<String> placeIdsOfDeal = placeIdsPerDeal.get(i);
      for (int j = 0; j < placeIdsOfDeal.size(); j++) {
        sb.append("place_id:");
        sb.append(placeIdsOfDeal.get(j));
        if (j != placeIdsOfDeal.size() - 1 || i != placeIdsPerDeal.size() - 1) {
          sb.append("|");
        }
      }
    }

    // Adds parameters to URL
    URIBuilder ub;
    try {
      ub = new URIBuilder("https://maps.googleapis.com/maps/api/distancematrix/json");
      ub.addParameter("origins", latitude + "," + longitude);
      ub.addParameter("destinations", sb.toString());
      ub.addParameter("key", API_KEY);
      System.out.println(ub.toString());
    } catch (URISyntaxException e) {
      return new ArrayList<>();
    }
    // Making a get request with the url
    HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(ub.toString()));
    String response = request.execute().parseAsString();

    // Converts json object to a DistanceResponse class
    DistanceResponse distanceResponse = gson.fromJson(response, DistanceResponse.class);

    // Creates a list of maps containing information about the placeids of a deal and its distance
    int placeIdIndex = 0;
    List<Map<String, Integer>> distanceDeals = new ArrayList<>();
    for (List<String> placeIds : placeIdsPerDeal) {
      try {
        Map<String, Integer> placeIdDist = new HashMap<>();
        for (int k = 0; k < placeIds.size(); k++) {
          placeIdDist.put(
              placeIds.get(k),
              Integer.parseInt(
                  distanceResponse.rows.get(0).elements.get(placeIdIndex + k).distance.value));
        }
        distanceDeals.add(placeIdDist);
      } catch (NullPointerException | IndexOutOfBoundsException e) {
        distanceDeals.add(new HashMap<>());
      }
      placeIdIndex += placeIds.size();
    }
    return distanceDeals;
  }

  /**
   * Returns the map api key read from the json file.
   *
   * @return the map api key.
   */
  private static String readMapApiKey() {
    String content;
    try {
      File file = new File(DistanceUtil.class.getResource("/api-key.json").getFile());
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
