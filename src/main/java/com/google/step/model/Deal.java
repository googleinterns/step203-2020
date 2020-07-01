package com.google.step.model;

public class Deal {
  public final long id;
  public final String description;
  public final String photoBlobkey;
  public final String start;
  public final String end;
  public final String source;
  public final long posterId;
  public final long restaurantId;

  public Deal(
      long id,
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long posterId,
      long restaurantId) {
    this.id = id;
    this.description = description;
    this.photoBlobkey = photoBlobkey;
    this.start = start;
    this.end = end;
    this.source = source;
    this.posterId = posterId;
    this.restaurantId = restaurantId;
  }
}
