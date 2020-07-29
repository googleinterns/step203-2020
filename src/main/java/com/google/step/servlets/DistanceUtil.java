package com.google.step.servlets;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.step.datamanager.RestaurantPlaceManager;
import com.google.step.datamanager.RestaurantPlaceManagerDatastore;
import com.google.step.model.Deal;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.utils.URIBuilder;

public class DistanceUtil {

  private static RestaurantPlaceManager restaurantPlaceManager;
  private static Gson gson = new Gson();
  private static final String API_KEY= "AIzaSyD7eIOONxtNsDb14Sr7uTtzQJDa7yNb9hI";

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

    public static Map<String, Integer> getDistances(Deal deal, String latitude, String longitude)
        throws IOException {
      HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
      StringBuilder sb = new StringBuilder();
      Set<String> placeIds = restaurantPlaceManager.getPlaceIdsOfRestaurant(deal.restaurantId);
      List<String> placeIdsList = new ArrayList<>(placeIds);
      int i = 0;
      for (String placeId : placeIdsList) {
        sb.append(placeId);
        if (i != placeIds.size() - 1) {
          sb.append("|");
        }
        i++;
      }
      try {
        URIBuilder ub = new URIBuilder("https://maps.googleapis.com/maps/api/distancematrix/json");
        ub.addParameter("origins", latitude + "," + longitude);
        ub.addParameter("destinations", sb.toString());
        ub.addParameter("key", API_KEY);
        System.out.println(ub.toString());
      } catch (URISyntaxException e) {
        return new HashMap<>();
      }

      HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(sb.toString()));
      String response = request.execute().parseAsString();
      DistanceResponse distanceResponse = gson.fromJson(response, DistanceResponse.class);
      try {
        return IntStream.range(0, placeIdsList.size())
            .boxed()
              .collect(
                Collectors.toMap(
                    j -> placeIdsList.get(j),
                    j ->
                        Integer.parseInt(
                            distanceResponse.rows.get(0).elements.get(j).distance.value)));
      } catch (NullPointerException | IndexOutOfBoundsException e) {
        return new HashMap<>();
      }
    }
  }
}
