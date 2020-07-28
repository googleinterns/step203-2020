package com.google.step.servlets;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.cloud.spanner.SpannerException;
import com.google.gson.Gson;
import com.google.spanner.LibraryFunctions;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;
@WebServlet("/api/distance")
public class DistanceServlet extends HttpServlet {
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

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String latitude = request.getParameter("latitude");
    String longitude = request.getParameter("longitude");
    String section = request.getParameter("section");
    String deals = 
    Map<String, Integer> distances =
        getDistances(
            deals.stream().map(deal -> deal.restaurant.getStoreAddress()).collect(Collectors.toList()),
            );
    if (!distances.isEmpty()) {
      stores =
          stores.stream()
              .filter(
                  store ->
                      (distances.get(store.getStoreAddress())
                          < (userPreferences.getDistancePreference() * MILES_TO_METERS)))
              .collect(Collectors.toList());
    }
  }

  public Map<String, Integer> getDistances(
      List<String> addresses, String latitude, String longitude) throws IOException {
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < addresses.size(); i++) {
      sb.append(addresses.get(i));
      if (i != addresses.size() - 1) {
        sb.append("|");
      }
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
    DistanceResponse distanceResponse = g.fromJson(response, DistanceResponse.class);
    try {
      return IntStream.range(0, addresses.size())
          .boxed()
          .collect(
              Collectors.toMap(
                  i -> addresses.get(i),
                  i ->
                      Integer.parseInt(
                          distanceResponse.rows.get(0).elements.get(i).distance.value)));
    } catch (NullPointerException | IndexOutOfBoundsException e) {
      return new HashMap<>();
    }
  }
}
