package com.google.step.servlets;

import com.google.gson.Gson;
import com.google.step.model.Deal;
import java.util.HashMap;
import java.util.Map;

public class JsonFormatter {
  public static String getDealJson(Deal deal) {
    Gson gson = new Gson();
    String json = gson.toJson(getDealMap(deal));
    return json;
  }

  private static Map<String, Object> getDealMap(Deal deal) {
    Map<String, Object> dealMap = new HashMap<>();
    dealMap.put("id", deal.id);
    dealMap.put("description", deal.description);
    dealMap.put("pic", getImageUrl(deal.photoBlobkey));
    dealMap.put("start", deal.start.toString());
    dealMap.put("end", deal.end.toString());
    dealMap.put("source", deal.source);
    dealMap.put("poster", deal.posterId); // TODO user brief
    dealMap.put("restaurant", deal.restaurantId); // TODO use restaurant brief
    dealMap.put("tags", "TODO"); // TODO add tags
    dealMap.put("votes", 0); // TODO add votes
    return dealMap;
  }

  private static Map<String, Object> getBriefDealMap(Deal deal) {
    Map<String, Object> dealMap = new HashMap<>();
    dealMap.put("id", deal.id);
    dealMap.put("description", deal.description);
    dealMap.put("pic", deal.photoBlobkey); // TODO get url
    dealMap.put("poster", deal.posterId); // TODO use user name
    dealMap.put("restaurant", deal.restaurantId); // TODO use restaurant name
    dealMap.put("votes", 0); // TODO add votes
    return dealMap;
  }

  private static String getImageUrl(String blobKey) {
    return "/api/images/" + blobKey;
  }
}
