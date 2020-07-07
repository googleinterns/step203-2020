package com.google.step.servlets;

import com.google.gson.Gson;
import com.google.step.model.Comment;
import com.google.step.model.Deal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonFormatter {
  public static String getCommentsJson(List<Comment> comments) {
    Gson gson = new Gson();
    List<Map<String, Object>> commentMapList = new ArrayList<Map<String, Object>>();
    for (Comment comment : comments) {
      commentMapList.add(getCommentMap(comment));
    }
    String json = gson.toJson(commentMapList);
    return json;
  }

  public static String getCommentJson(Comment comment) {
    Gson gson = new Gson();
    String json = gson.toJson(getCommentMap(comment));
    return json;
  }

  public static String getDealJson(Deal deal) {
    Gson gson = new Gson();
    String json = gson.toJson(getDealMap(deal));
    return json;
  }

  private static Map<String, Object> getCommentMap(Comment comment) {
    Map<String, Object> commentMap = new HashMap<>();
    commentMap.put("id", comment.id);
    commentMap.put("dealId", comment.dealId);
    commentMap.put("userId", comment.userId);
    commentMap.put("content", comment.content);
    return commentMap;
  }

  private static Map<String, Object> getDealMap(Deal deal) {
    Map<String, Object> dealMap = new HashMap<>();
    dealMap.put("id", deal.id);
    dealMap.put("description", deal.description);
    dealMap.put("pic", deal.photoBlobkey); // TODO get url
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
}
