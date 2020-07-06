package com.google.step.model;

public class Restaurant {
  public final long id;
  public final String name;
  public final String photoBlobkey;

  public Restaurant(long id, String name, String photoBlobkey) {
    this.id = id;
    this.name = name;
    this.photoBlobkey = photoBlobkey;
  }
}
