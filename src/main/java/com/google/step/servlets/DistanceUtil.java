package com.google.step.servlets;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.step.datamanager.RestaurantPlaceManager;
import com.google.step.model.Deal;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.client.utils.URIBuilder;

public class DistanceUtil {

  private static Gson gson = new Gson();
  private static final String API_KEY = "AIzaSyD7eIOONxtNsDb14Sr7uTtzQJDa7yNb9hI";

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
      List<Deal> deals,
      String latitude,
      String longitude,
      RestaurantPlaceManager restaurantPlaceManager)
      throws IOException {
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
    StringBuilder sb = new StringBuilder();
    List<List<String>> placeIdsPerDeal = new ArrayList<>();
    for (int i = 0; i < deals.size(); i++) {
      Set<String> placeIds =
          restaurantPlaceManager.getPlaceIdsOfRestaurant(deals.get(i).restaurantId);
      List<String> placeIdsList = new ArrayList<>(placeIds);
      placeIdsPerDeal.add(placeIdsList);
      for (int j = 0; j < placeIdsList.size(); j++) {
        sb.append("place_id:");
        sb.append(placeIdsList.get(j));
        if (j != placeIds.size() - 1 || i != deals.size() - 1) {
          sb.append("|");
        }
      }
    }
    URIBuilder ub;
    try {
      ub = new URIBuilder("https://maps.googleapis.com/maps/api/distancematrix/json");
      ub.addParameter("origins", latitude + "," + longitude);
      ub.addParameter("destinations", sb.toString());
      ub.addParameter("key", API_KEY);
      // System.out.println(ub.toString());
    } catch (URISyntaxException e) {
      return new ArrayList<>();
    }
    HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(ub.toString()));
    String response = request.execute().parseAsString();
    DistanceResponse distanceResponse = gson.fromJson(response, DistanceResponse.class);
    int j = 0;
    List<Map<String, Integer>> distanceDeals = new ArrayList<>();
    for (List<String> placeIds : placeIdsPerDeal) {
      try {

        Map<String, Integer> placeIdDist = new HashMap<>();
        for (int k = 0; k < placeIds.size(); k++) {
          placeIdDist.put(
              placeIds.get(k),
              Integer.parseInt(distanceResponse.rows.get(0).elements.get(j + k).distance.value));
        }
        distanceDeals.add(placeIdDist);
      } catch (NullPointerException | IndexOutOfBoundsException e) {
        distanceDeals.add(new HashMap<>());
      }
      j += placeIds.size();
    }
    return distanceDeals;
  }
}
