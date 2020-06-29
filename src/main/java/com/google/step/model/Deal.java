package com.google.step.model;

public class Deal {
  public final long id;
  public final String description;
  public final String photoBlobkey;
  public final String start;
  public final String end;
  public final String source;
  public final long poster;
  public final long restaurant;

  public Deal(
      long id,
      String description,
      String photoBlobkey,
      String start,
      String end,
      String source,
      long poster,
      long restaurant) {
    this.id = id;
    this.description = description;
    this.photoBlobkey = photoBlobkey;
    this.start = start;
    this.end = end;
    this.source = source;
    this.poster = poster;
    this.restaurant = restaurant;
  }
}
